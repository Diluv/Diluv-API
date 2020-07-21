package com.diluv.api.v1.projects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

import com.diluv.api.data.DataProject;
import com.diluv.api.data.DataProjectAuthorized;
import com.diluv.api.data.DataProjectFileInQueue;
import com.diluv.api.utils.FileUtil;
import com.diluv.api.utils.MismatchException;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.api.v1.games.ProjectFileUploadForm;
import com.diluv.confluencia.database.record.GameVersionsEntity;
import com.diluv.confluencia.database.record.ProjectFileDependenciesEntity;
import com.diluv.confluencia.database.record.ProjectFileGameVersionsEntity;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.UsersEntity;

import static com.diluv.api.Main.DATABASE;

@GZIP
@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectsAPI {

    @Cache(maxAge = 30, mustRevalidate = true)
    @GET
    @Path("/{projectId}")
    public Response getProject (@HeaderParam("Authorization") Token token, @PathParam("projectId") Long projectId) {

        final ProjectsEntity project = DATABASE.projectDAO.findOneProjectByProjectId(projectId);
        if (project == null || !project.isReleased() && token == null) {
            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        if (token != null) {
            List<String> permissions = ProjectPermissions.getAuthorizedUserPermissions(project, token);

            if (permissions != null) {
                return ResponseUtil.successResponse(new DataProjectAuthorized(project, permissions));
            }
        }

        return ResponseUtil.successResponse(new DataProject(project));
    }

    @POST
    @Path("/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postProjectFile (@HeaderParam("Authorization") Token token, @MultipartForm ProjectFileUploadForm form) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        if (form.projectId == null) {
            return ErrorMessage.PROJECT_FILE_INVALID_PROJECT_ID.respond();
        }

        final ProjectsEntity project = DATABASE.projectDAO.findOneProjectByProjectId(form.projectId);

        if (project == null) {
            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        if (!ProjectPermissions.hasPermission(project, token, ProjectPermissions.FILE_UPLOAD)) {

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

        List<GameVersionsEntity> gameVersionRecords;
        try {
            gameVersionRecords = Validator.validateGameVersions(project.getGame(), form.gameVersions);
        }
        catch (MismatchException e) {
            return e.getErrorMessage().respond();
        }

        List<ProjectsEntity> dependencyRecords;
        try {
            dependencyRecords = Validator.validateDependencies(form.projectId, form.dependencies);
        }
        catch (MismatchException e) {
            return e.getErrorMessage().respond();
        }
        catch (NumberFormatException e) {
            return ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_ID.respond();
        }

        final String fileName = FilenameUtils.getName(form.fileName);
        final File tempFile = FileUtil.getTempFile(project.getId(), fileName);
        final String sha512 = FileUtil.writeFile(form.file, project.getProjectType().getMaxFileSize(), tempFile);

        if (tempFile == null) {

            return ErrorMessage.FAILED_TEMP_FILE.respond();
        }

        if (sha512 == null) {

            return ErrorMessage.FAILED_SHA512.respond();
        }

        ProjectFilesEntity projectFile = new ProjectFilesEntity();
        projectFile.setName(fileName);
        projectFile.setVersion(form.version);
        projectFile.setSize(tempFile.length());
        projectFile.setChangelog(form.changelog);
        projectFile.setSha512(sha512);
        projectFile.setReleaseType(form.releaseType);
        projectFile.setClassifier(form.classifier);
        projectFile.setProject(project);
        projectFile.setUser(new UsersEntity(token.getUserId()));

        if (!gameVersionRecords.isEmpty()) {
            List<ProjectFileGameVersionsEntity> tagIds = new ArrayList<>();
            for (GameVersionsEntity gameVersions : gameVersionRecords) {
                ProjectFileGameVersionsEntity gameVersionsEntity = new ProjectFileGameVersionsEntity();
                gameVersionsEntity.setGameVersion(gameVersions);
                tagIds.add(gameVersionsEntity);
            }
            projectFile.setGameVersions(tagIds);
        }

        if (!dependencyRecords.isEmpty()) {
            List<ProjectFileDependenciesEntity> tagIds = new ArrayList<>();
            for (ProjectsEntity dep : dependencyRecords) {
                ProjectFileDependenciesEntity dependency = new ProjectFileDependenciesEntity();
                dependency.setDependencyProject(dep);
                tagIds.add(dependency);
            }
            projectFile.setDependencies(tagIds);
        }

        if (!DATABASE.fileDAO.insertProjectFile(projectFile)) {

            return ErrorMessage.FAILED_CREATE_PROJECT_FILE.respond();
        }


        projectFile = DATABASE.fileDAO.findOneById(projectFile.getId());
        if (projectFile == null) {
            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        final String gameSlug = project.getGame().getSlug();
        final String projectTypeSlug = project.getProjectType().getSlug();

        File destination = FileUtil.getOutputLocation(gameSlug, projectTypeSlug, project.getId(), projectFile.getId(), fileName);
        destination.getParentFile().mkdirs();
        final boolean moved = tempFile.renameTo(destination);

        tempFile.delete();
        tempFile.getParentFile().delete();

        if (!moved) {
            return ErrorMessage.ERROR_WRITING.respond();
        }

        return ResponseUtil.successResponse(new DataProjectFileInQueue(projectFile, gameSlug, projectTypeSlug, project.getSlug()));
    }
}
