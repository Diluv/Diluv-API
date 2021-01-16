package com.diluv.api.v1.projects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.diluv.api.v1.games.GamesAPI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.GenericValidator;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.Query;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataGameVersion;
import com.diluv.api.data.DataProject;
import com.diluv.api.data.DataProjectFileInQueue;
import com.diluv.api.data.DataProjectFileList;
import com.diluv.api.data.site.DataSiteProjectFileDisplay;
import com.diluv.api.data.site.DataSiteProjectFilesPage;
import com.diluv.api.provider.ResponseException;
import com.diluv.api.utils.FileUtil;
import com.diluv.api.utils.MismatchException;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.auth.tokens.ErrorToken;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.api.utils.query.ProjectFileQuery;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.api.v1.games.ProjectFileUploadForm;
import com.diluv.api.v1.utilities.ProjectService;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.GameVersionsEntity;
import com.diluv.confluencia.database.record.ProjectFileDependenciesEntity;
import com.diluv.confluencia.database.record.ProjectFileGameVersionsEntity;
import com.diluv.confluencia.database.record.ProjectFileLoadersEntity;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.diluv.confluencia.database.record.ProjectTypeLoadersEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.UsersEntity;
import com.diluv.confluencia.database.sort.ProjectFileSort;
import com.diluv.confluencia.database.sort.Sort;

import org.jboss.resteasy.spi.validation.GeneralValidator;

@GZIP
@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectsAPI {

    @GET
    @Path("/{id}")
    public Response getProject (@HeaderParam("Authorization") Token token, @PathParam("id") Long id) {

        return Confluencia.getTransaction(session -> {
            try {
                final DataBaseProject project = ProjectService.getDataProject(session, id, token);
                return ResponseUtil.successResponse(project);
            }
            catch (ResponseException e) {
                e.printStackTrace();
                return e.getResponse();
            }
        });
    }

    @POST
    @Path("/{id}/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postProjectFile (@HeaderParam("Authorization") Token token, @PathParam("id") Long projectId, @MultipartForm ProjectFileUploadForm form) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        if (token instanceof ErrorToken) {
            return ((ErrorToken) token).getResponse();
        }

        if (form.data == null) {
            return ErrorMessage.INVALID_DATA.respond();
        }

        return Confluencia.getTransaction(session -> {
            final ProjectsEntity project = Confluencia.PROJECT.findOneProjectByProjectId(session, projectId);

            if (project == null) {
                return ErrorMessage.NOT_FOUND_PROJECT.respond();
            }

            if (!ProjectPermissions.hasPermission(project, token, ProjectPermissions.FILE_UPLOAD)) {

                return ErrorMessage.USER_NOT_AUTHORIZED.respond();
            }

            if (!Validator.validateProjectFileChangelog(form.data.changelog)) {

                return ErrorMessage.PROJECT_FILE_INVALID_CHANGELOG.respond();
            }

            if (form.file == null) {

                return ErrorMessage.PROJECT_FILE_INVALID_FILE.respond();
            }

            if (form.fileName == null) {

                return ErrorMessage.PROJECT_FILE_INVALID_FILENAME.respond();
            }

            if (!Validator.validateReleaseType(form.data.releaseType)) {

                return ErrorMessage.PROJECT_FILE_INVALID_RELEASE_TYPE.respond();
            }

            if (!Validator.validateClassifier(form.data.classifier)) {

                return ErrorMessage.PROJECT_FILE_INVALID_CLASSIFIER.respond();
            }

            if (!Validator.validateVersion(form.data.version)) {

                return ErrorMessage.PROJECT_FILE_INVALID_VERSION.respond();
            }

            if (Confluencia.FILE.existsByProjectIdAndVersion(session, projectId, form.data.version)) {

                return ErrorMessage.PROJECT_FILE_TAKEN_VERSION.respond();
            }

            List<GameVersionsEntity> gameVersionRecords;
            try {
                gameVersionRecords = Validator.validateGameVersions(project.getGame(), form.data.gameVersions);
            }
            catch (MismatchException e) {
                return e.getErrorMessage().respond(e.getMessage());
            }

            List<ProjectTypeLoadersEntity> projectTypeLoaders;
            try {
                projectTypeLoaders = Validator.validateProjectTypeLoaders(project.getProjectType(), form.data.loaders);
            }
            catch (MismatchException e) {
                return e.getErrorMessage().respond(e.getMessage());
            }

            List<ProjectFileDependenciesEntity> dependencyRecords;
            try {
                dependencyRecords = Validator.validateDependencies(session, projectId, form.data.dependencies);
            }
            catch (MismatchException e) {
                if (e.getMessage() == null) {
                    return e.getErrorMessage().respond();
                }
                return e.getErrorMessage().respond(e.getMessage());
            }
            catch (NumberFormatException e) {
                return ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_ID.respond();
            }

            final String fileName = FilenameUtils.getName(form.fileName);
            if (GenericValidator.isBlankOrNull(fileName) || !(form.fileName.equals(fileName))) {
                return ErrorMessage.PROJECT_FILE_INVALID_FILENAME.respond();
            }
            final File tempFile = FileUtil.getTempFile(project.getId(), fileName);
            final String sha512 = FileUtil.writeFile(form.file, project.getProjectType().getMaxFileSize(), tempFile);

            if (tempFile == null) {
                System.out.println("FAILED_TEMP_FILE");
                // return ErrorMessage.FAILED_TEMP_FILE.respond();
                return ErrorMessage.THROWABLE.respond();
            }

            if (sha512 == null) {
                System.out.println("FAILED_SHA512");
                //return ErrorMessage.FAILED_SHA512.respond();
                return ErrorMessage.THROWABLE.respond();
            }

            ProjectFilesEntity projectFile = new ProjectFilesEntity();
            projectFile.setName(fileName);
            projectFile.setVersion(form.data.version);
            projectFile.setSize(tempFile.length());
            projectFile.setChangelog(form.data.changelog);
            projectFile.setSha512(sha512);
            projectFile.setReleaseType(form.data.releaseType);
            projectFile.setClassifier(form.data.classifier);
            projectFile.setProject(project);
            projectFile.setUser(new UsersEntity(token.getUserId()));

            if (!gameVersionRecords.isEmpty()) {
                List<ProjectFileGameVersionsEntity> gameVersions = new ArrayList<>();
                for (GameVersionsEntity version : gameVersionRecords) {
                    ProjectFileGameVersionsEntity gameVersionsEntity = new ProjectFileGameVersionsEntity();
                    gameVersionsEntity.setProjectFile(projectFile);
                    gameVersionsEntity.setGameVersion(version);
                    gameVersions.add(gameVersionsEntity);
                }
                projectFile.setGameVersions(gameVersions);
            }

            if (!projectTypeLoaders.isEmpty()) {
                List<ProjectFileLoadersEntity> loaders = new ArrayList<>();
                for (ProjectTypeLoadersEntity loader : projectTypeLoaders) {
                    ProjectFileLoadersEntity fileLoadersEntity = new ProjectFileLoadersEntity();
                    fileLoadersEntity.setProjectFile(projectFile);
                    fileLoadersEntity.setLoader(loader);
                    loaders.add(fileLoadersEntity);
                }
                projectFile.setLoaders(loaders);
            }

            if (!dependencyRecords.isEmpty()) {
                List<ProjectFileDependenciesEntity> dependencies = new ArrayList<>();
                for (ProjectFileDependenciesEntity dependency : dependencyRecords) {
                    dependency.setProjectFile(projectFile);
                    dependencies.add(dependency);
                }
                projectFile.setDependencies(dependencies);
            }

            session.save(projectFile);
            session.flush();
            session.refresh(projectFile);

            final String gameSlug = project.getGame().getSlug();
            final String projectTypeSlug = project.getProjectType().getSlug();

            File destination = FileUtil.getOutputLocation(gameSlug, projectTypeSlug, project.getId(), projectFile.getId(), fileName);
            destination.getParentFile().mkdirs();
            try {
                FileUtils.copyFile(tempFile, destination);
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("ERROR_WRITING");
                return ErrorMessage.THROWABLE.respond();
            } finally {
                tempFile.delete();
                tempFile.getParentFile().delete();
            }

            return ResponseUtil.successResponse(new DataProjectFileInQueue(projectFile, gameSlug, projectTypeSlug, project.getSlug()));
        });
    }

    @POST
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postProjectFile (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug, @MultipartForm ProjectFileUploadForm form) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        if (token instanceof ErrorToken) {
            return ((ErrorToken) token).getResponse();
        }

        if (form.data == null) {
            return ErrorMessage.INVALID_DATA.respond();
        }

        return Confluencia.getTransaction(session -> {
            final ProjectsEntity project = Confluencia.PROJECT.findOneProject(session, gameSlug,projectTypeSlug, projectSlug);

            if (project == null) {
                return ErrorMessage.NOT_FOUND_PROJECT.respond();
            }

            if (!ProjectPermissions.hasPermission(project, token, ProjectPermissions.FILE_UPLOAD)) {

                return ErrorMessage.USER_NOT_AUTHORIZED.respond();
            }

            if (!Validator.validateProjectFileChangelog(form.data.changelog)) {

                return ErrorMessage.PROJECT_FILE_INVALID_CHANGELOG.respond();
            }

            if (form.file == null) {

                return ErrorMessage.PROJECT_FILE_INVALID_FILE.respond();
            }

            if (form.fileName == null) {

                return ErrorMessage.PROJECT_FILE_INVALID_FILENAME.respond();
            }

            if (!Validator.validateReleaseType(form.data.releaseType)) {

                return ErrorMessage.PROJECT_FILE_INVALID_RELEASE_TYPE.respond();
            }

            if (!Validator.validateClassifier(form.data.classifier)) {

                return ErrorMessage.PROJECT_FILE_INVALID_CLASSIFIER.respond();
            }

            if (!Validator.validateVersion(form.data.version)) {

                return ErrorMessage.PROJECT_FILE_INVALID_VERSION.respond();
            }

            if (Confluencia.FILE.existsByProjectIdAndVersion(session, project.getId(), form.data.version)) {

                return ErrorMessage.PROJECT_FILE_TAKEN_VERSION.respond();
            }

            List<GameVersionsEntity> gameVersionRecords;
            try {
                gameVersionRecords = Validator.validateGameVersions(project.getGame(), form.data.gameVersions);
            }
            catch (MismatchException e) {
                return e.getErrorMessage().respond(e.getMessage());
            }

            List<ProjectTypeLoadersEntity> projectTypeLoaders;
            try {
                projectTypeLoaders = Validator.validateProjectTypeLoaders(project.getProjectType(), form.data.loaders);
            }
            catch (MismatchException e) {
                return e.getErrorMessage().respond(e.getMessage());
            }

            List<ProjectFileDependenciesEntity> dependencyRecords;
            try {
                dependencyRecords = Validator.validateDependencies(session, project.getId(), form.data.dependencies);
            }
            catch (MismatchException e) {
                if (e.getMessage() == null) {
                    return e.getErrorMessage().respond();
                }
                return e.getErrorMessage().respond(e.getMessage());
            }
            catch (NumberFormatException e) {
                return ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_ID.respond();
            }

            final String fileName = FilenameUtils.getName(form.fileName);
            final File tempFile = FileUtil.getTempFile(project.getId(), fileName);
            final String sha512 = FileUtil.writeFile(form.file, project.getProjectType().getMaxFileSize(), tempFile);

            if (tempFile == null) {
                System.out.println("FAILED_TEMP_FILE");
                // return ErrorMessage.FAILED_TEMP_FILE.respond();
                return ErrorMessage.THROWABLE.respond();
            }

            if (sha512 == null) {
                System.out.println("FAILED_SHA512");
                //return ErrorMessage.FAILED_SHA512.respond();
                return ErrorMessage.THROWABLE.respond();
            }

            ProjectFilesEntity projectFile = new ProjectFilesEntity();
            projectFile.setName(fileName);
            projectFile.setVersion(form.data.version);
            projectFile.setSize(tempFile.length());
            projectFile.setChangelog(form.data.changelog);
            projectFile.setSha512(sha512);
            projectFile.setReleaseType(form.data.releaseType);
            projectFile.setClassifier(form.data.classifier);
            projectFile.setProject(project);
            projectFile.setUser(new UsersEntity(token.getUserId()));

            if (!gameVersionRecords.isEmpty()) {
                List<ProjectFileGameVersionsEntity> gameVersions = new ArrayList<>();
                for (GameVersionsEntity version : gameVersionRecords) {
                    ProjectFileGameVersionsEntity gameVersionsEntity = new ProjectFileGameVersionsEntity();
                    gameVersionsEntity.setProjectFile(projectFile);
                    gameVersionsEntity.setGameVersion(version);
                    gameVersions.add(gameVersionsEntity);
                }
                projectFile.setGameVersions(gameVersions);
            }

            if (!projectTypeLoaders.isEmpty()) {
                List<ProjectFileLoadersEntity> loaders = new ArrayList<>();
                for (ProjectTypeLoadersEntity loader : projectTypeLoaders) {
                    ProjectFileLoadersEntity fileLoadersEntity = new ProjectFileLoadersEntity();
                    fileLoadersEntity.setProjectFile(projectFile);
                    fileLoadersEntity.setLoader(loader);
                    loaders.add(fileLoadersEntity);
                }
                projectFile.setLoaders(loaders);
            }

            if (!dependencyRecords.isEmpty()) {
                List<ProjectFileDependenciesEntity> dependencies = new ArrayList<>();
                for (ProjectFileDependenciesEntity dependency : dependencyRecords) {
                    dependency.setProjectFile(projectFile);
                    dependencies.add(dependency);
                }
                projectFile.setDependencies(dependencies);
            }

            session.save(projectFile);
            session.flush();
            session.refresh(projectFile);

            File destination = FileUtil.getOutputLocation(gameSlug, projectTypeSlug, project.getId(), projectFile.getId(), fileName);
            destination.getParentFile().mkdirs();
            try {
                FileUtils.copyFile(tempFile, destination);
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("ERROR_WRITING");
                return ErrorMessage.THROWABLE.respond();
            } finally {
                tempFile.delete();
                tempFile.getParentFile().delete();
            }

            return ResponseUtil.successResponse(new DataProjectFileInQueue(projectFile, gameSlug, projectTypeSlug, project.getSlug()));
        });
    }

    @GET
    @Path("/files/{fileId}")
    public Response getProjectFile (@HeaderParam("Authorization") Token token, @PathParam("fileId") long fileId) {

        return Confluencia.getTransaction(session -> {
            final ProjectFilesEntity projectFile = Confluencia.FILE.findOneById(session, fileId);

            if (projectFile == null) {
                return ErrorMessage.NOT_FOUND_PROJECT_FILE.respond();
            }
            final ProjectsEntity project = projectFile.getProject();
            boolean authorized = ProjectPermissions.hasPermission(project, token, ProjectPermissions.FILE_EDIT);

            if (!projectFile.isReleased() && !authorized) {
                return ErrorMessage.NOT_FOUND_PROJECT_FILE.respond();
            }

            final List<GameVersionsEntity> gameVersionRecords = projectFile.getGameVersions().stream().map(ProjectFileGameVersionsEntity::getGameVersion).collect(Collectors.toList());
            final List<DataGameVersion> gameVersions = gameVersionRecords.stream().map(DataGameVersion::new).collect(Collectors.toList());

            return ResponseUtil.successResponse(new DataSiteProjectFileDisplay(projectFile, gameVersions));
        });
    }

    @GET
    @Path("/hash/{hash}")
    public Response getProjectByHash (@PathParam("hash") String projectFileHash, @Query ProjectFileQuery query) {

        final long page = query.getPage();
        final int limit = query.getLimit();
        final Sort sort = query.getSort(ProjectFileSort.NEW);

        return Confluencia.getTransaction(session -> {
            final List<ProjectFilesEntity> projectFiles = Confluencia.FILE.findProjectFilesByHash(session, projectFileHash, page, limit, sort);

            DataBaseProject lastProject = null;
            Map<DataBaseProject, List<DataSiteProjectFileDisplay>> projectFileMap = new HashMap<>();
            for (ProjectFilesEntity rs : projectFiles) {

                long projectId = rs.getProject().getId();

                if (lastProject == null || lastProject.getId() != projectId) {
                    lastProject = projectFileMap.keySet().stream().filter(a -> a.getId() == projectId).findAny().orElse(null);
                }

                final List<GameVersionsEntity> gameVersionRecords = rs.getGameVersions().stream().map(ProjectFileGameVersionsEntity::getGameVersion).collect(Collectors.toList());
                final List<DataGameVersion> gameVersions = gameVersionRecords.stream().map(DataGameVersion::new).collect(Collectors.toList());

                DataSiteProjectFileDisplay file = new DataSiteProjectFileDisplay(rs, gameVersions);
                List<DataSiteProjectFileDisplay> fileDisplayList = projectFileMap.getOrDefault(lastProject, new ArrayList<>());
                fileDisplayList.add(file);
                if (lastProject == null) {
                    projectFileMap.put(new DataBaseProject(rs.getProject()), fileDisplayList);
                }
            }

            List<DataSiteProjectFilesPage> projectFilesPages = new ArrayList<>();
            projectFileMap.forEach((key, value) -> projectFilesPages.add(new DataSiteProjectFilesPage(key, value, value.size(), GamesAPI.PROJECT_FILE_SORTS)));
            return ResponseUtil.successResponse(new DataProjectFileList(projectFilesPages));
        });
    }
}
