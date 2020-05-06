package com.diluv.api.v1.projects;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.diluv.api.data.DataCategory;
import com.diluv.api.data.DataGameVersion;
import com.diluv.api.data.DataProject;
import com.diluv.api.data.DataProjectAuthorized;
import com.diluv.api.data.DataProjectContributor;
import com.diluv.api.data.DataProjectContributorAuthorized;
import com.diluv.api.data.DataProjectFileInQueue;
import com.diluv.api.data.DataProjectLink;
import com.diluv.api.utils.FileUtil;
import com.diluv.api.utils.MismatchException;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.api.v1.games.ProjectFileUploadForm;
import com.diluv.confluencia.database.record.CategoryRecord;
import com.diluv.confluencia.database.record.GameVersionRecord;
import com.diluv.confluencia.database.record.ProjectAuthorRecord;
import com.diluv.confluencia.database.record.ProjectFileRecord;
import com.diluv.confluencia.database.record.ProjectLinkRecord;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.diluv.confluencia.database.record.ProjectTypeRecord;

import static com.diluv.api.Main.DATABASE;

@GZIP
@Path("/projects")
public class ProjectsAPI {

    @Cache(maxAge = 30, mustRevalidate = true)
    @GET
    @Path("/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject (@HeaderParam("Authorization") Token token, @PathParam("projectId") Long projectId) {

        final ProjectRecord projectRecord = DATABASE.projectDAO.findOneProjectByProjectId(projectId);
        if (projectRecord == null || !projectRecord.isReleased() && token == null) {
            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        final List<ProjectLinkRecord> projectLinkRecords = DATABASE.projectDAO.findAllLinksByProjectId(projectRecord.getId());
        final List<DataProjectLink> projectLinks = projectLinkRecords.stream().map(DataProjectLink::new).collect(Collectors.toList());

        final List<ProjectAuthorRecord> records = DATABASE.projectDAO.findAllProjectAuthorsByProjectId(projectRecord.getId());

        final List<CategoryRecord> categoryRecords = DATABASE.projectDAO.findAllCategoriesByProjectId(projectRecord.getId());
        List<DataCategory> categories = categoryRecords.stream().map(DataCategory::new).collect(Collectors.toList());

        if (token != null) {
            List<String> permissions = ProjectPermissions.getAuthorizedUserPermissions(projectRecord, token, records);

            if (permissions != null) {
                final List<DataProjectContributor> projectAuthors = records.stream().map(DataProjectContributorAuthorized::new).collect(Collectors.toList());
                return ResponseUtil.successResponse(new DataProjectAuthorized(projectRecord, categories, projectAuthors, projectLinks, permissions));
            }
        }

        final List<DataProjectContributor> projectAuthors = records.stream().map(DataProjectContributor::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(new DataProject(projectRecord, categories, projectAuthors, projectLinks));
    }

    @POST
    @Path("/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postProjectFile (@HeaderParam("Authorization") Token token, @MultipartForm ProjectFileUploadForm form) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        if (form.projectId == null) {
            return ErrorMessage.PROJECT_FILE_INVALID_PROJECT_ID.respond();
        }

        final ProjectRecord projectRecord = DATABASE.projectDAO.findOneProjectByProjectId(form.projectId);

        if (projectRecord == null) {
            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        final String gameSlug = projectRecord.getGameSlug();
        final String projectTypeSlug = projectRecord.getProjectTypeSlug();

        if (!ProjectPermissions.hasPermission(projectRecord, token, ProjectPermissions.FILE_UPLOAD)) {

            return ErrorMessage.USER_NOT_AUTHORIZED.respond();
        }

        if (!Validator.validateProjectFileChangelog(form.changelog)) {

            return ErrorMessage.PROJECT_FILE_INVALID_CHANGELOG.respond();
        }

        if (form.file == null) {

            return ErrorMessage.PROJECT_FILE_INVALID_FILE.respond();
        }

        if (form.fileName == null) {

            return ErrorMessage.PROJECT_FILE_INVALID_FILENAME.respond();
        }

        if (!Validator.validateReleaseType(form.releaseType)) {

            return ErrorMessage.PROJECT_FILE_INVALID_RELEASE_TYPE.respond();
        }

        if (!Validator.validateClassifier(form.classifier)) {

            return ErrorMessage.PROJECT_FILE_INVALID_CLASSIFIER.respond();
        }

        if (form.version == null || form.version.length() > 20) {

            return ErrorMessage.PROJECT_FILE_INVALID_VERSION.respond();
        }

        if (DATABASE.fileDAO.existsByProjectIdAndVersion(form.projectId, form.version)) {
            return ErrorMessage.PROJECT_FILE_TAKEN_VERSION.respond();
        }

        List<GameVersionRecord> gameVersionRecords;
        try {
            gameVersionRecords = Validator.validateGameVersions(gameSlug, form.gameVersions);
        }
        catch (MismatchException e) {
            return e.getErrorMessage().respond();
        }

        List<ProjectRecord> dependencyRecords;
        try {
            dependencyRecords = Validator.validateDependencies(form.projectId, form.dependencies);
        }
        catch (MismatchException e) {
            return e.getErrorMessage().respond();
        }
        catch (NumberFormatException e) {
            return ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_ID.respond();
        }

        final ProjectTypeRecord projectTypeRecord = DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);

        final String fileName = FilenameUtils.getName(form.fileName);
        final File tempFile = FileUtil.getTempFile(projectRecord.getId(), fileName);
        final String sha512 = FileUtil.writeFile(form.file, projectTypeRecord.getMaxFileSize(), tempFile);

        if (tempFile == null) {

            return ErrorMessage.FAILED_TEMP_FILE.respond();
        }

        if (sha512 == null) {

            return ErrorMessage.FAILED_SHA512.respond();
        }

        final Long projectFileId = DATABASE.fileDAO.insertProjectFile(fileName, form.version, tempFile.length(), form.changelog, sha512, form.releaseType.toLowerCase(), form.classifier.toLowerCase(), projectRecord.getId(), token.getUserId());

        if (projectFileId == null) {

            return ErrorMessage.FAILED_CREATE_PROJECT_FILE.respond();
        }

        if (!gameVersionRecords.isEmpty()) {
            List<Long> versionIds = gameVersionRecords.stream().map(GameVersionRecord::getId).collect(Collectors.toList());

            if (!DATABASE.fileDAO.insertProjectFileGameVersions(projectFileId, versionIds)) {
                return ErrorMessage.FAILED_CREATE_PROJECT_FILE_GAME_VERSION.respond();
            }
        }

        List<Long> dependencies = dependencyRecords.stream().map(ProjectRecord::getId).collect(Collectors.toList());

        if (!dependencyRecords.isEmpty()) {
            if (!DATABASE.fileDAO.insertProjectFileDependency(projectFileId, dependencies)) {
                return ErrorMessage.FAILED_CREATE_PROJECT_FILE_GAME_VERSION.respond();
            }
        }

        File destination = FileUtil.getOutputLocation(gameSlug, projectTypeSlug, projectRecord.getId(), projectFileId, fileName);
        destination.getParentFile().mkdirs();
        final boolean moved = tempFile.renameTo(destination);

        tempFile.delete();
        tempFile.getParentFile().delete();

        if (!moved) {
            return ErrorMessage.ERROR_WRITING.respond();
        }

        final ProjectFileRecord record = DATABASE.fileDAO.findOneProjectFileQueueByFileId(projectFileId);

        List<DataGameVersion> gameVersions = gameVersionRecords.stream().map(DataGameVersion::new).collect(Collectors.toList());

        return ResponseUtil.successResponse(new DataProjectFileInQueue(record, dependencies, gameVersions, gameSlug, projectTypeSlug, projectRecord.getSlug()));
    }
}
