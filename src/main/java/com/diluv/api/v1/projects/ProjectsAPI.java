package com.diluv.api.v1.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.GenericValidator;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.Query;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataGameVersion;
import com.diluv.api.data.DataProjectFileList;
import com.diluv.api.data.site.DataSiteProjectFileDisplay;
import com.diluv.api.data.site.DataSiteProjectFilesPage;
import com.diluv.api.provider.ResponseException;
import com.diluv.api.utils.MismatchException;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.api.utils.query.ProjectFileQuery;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.api.utils.validator.RequireToken;
import com.diluv.api.v1.games.ProjectFileUploadForm;
import com.diluv.api.v1.utilities.ProjectFileService;
import com.diluv.api.v1.utilities.ProjectService;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.GameVersionsEntity;
import com.diluv.confluencia.database.record.ProjectFileDependenciesEntity;
import com.diluv.confluencia.database.record.ProjectFileGameVersionsEntity;
import com.diluv.confluencia.database.record.ProjectFileLoadersEntity;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.diluv.confluencia.database.record.ProjectTypeLoadersEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.sort.ProjectFileSort;
import com.diluv.confluencia.database.sort.Sort;

@ApplicationScoped
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
                return e.getResponse();
            }
        });
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProject (@RequireToken @HeaderParam("Authorization") Token token, @PathParam("id") Long id) {

        return Confluencia.getTransaction(session -> {
            try {
                final ProjectsEntity project = ProjectService.getProject(session, id, token);
                if (project.getOwner().getId() == token.getUserId()) {
                    session.delete(project);
                    return ResponseUtil.noContent();
                }
                else {
                    return ErrorMessage.USER_NOT_AUTHORIZED.respond();
                }
            }
            catch (ResponseException e) {
                return e.getResponse();
            }
        });
    }

    @POST
    @Path("/{id}/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadProjectFile (@RequireToken @HeaderParam("Authorization") Token token, @PathParam("id") Long projectId, @Valid @MultipartForm ProjectFileUploadForm form) {

        return Confluencia.getTransaction(session -> {
            try {
                ProjectsEntity project = ProjectService.getProject(session, projectId, token);
                return ProjectFileService.postProjectFile(session, project, token, form);
            }
            catch (ResponseException e) {
                return e.getResponse();
            }
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

    @PATCH
    @Path("/files/{fileId}")
    public Response editProjectFile (@HeaderParam("Authorization") Token token, @PathParam("fileId") long fileId, @Valid @MultipartForm ProjectFilePatchForm form) {

        return Confluencia.getTransaction(session -> {
            final ProjectFilesEntity projectFile = Confluencia.FILE.findOneById(session, fileId);

            if (projectFile == null) {
                return ErrorMessage.NOT_FOUND_PROJECT_FILE.respond();
            }
            final ProjectsEntity project = projectFile.getProject();
            boolean authorized = ProjectPermissions.hasPermission(project, token, ProjectPermissions.FILE_EDIT);

            if (!authorized) {
                if (!projectFile.isReleased()) {
                    return ErrorMessage.NOT_FOUND_PROJECT_FILE.respond();
                }
                return ErrorMessage.USER_NOT_AUTHORIZED.respond();
            }

            final String displayName = FilenameUtils.getName(form.data.displayName);

            if (!GenericValidator.isBlankOrNull(displayName)) {
                if (!form.data.displayName.equals(displayName)) {
                    return ErrorMessage.PROJECT_FILE_INVALID_DISPLAY_NAME.respond();
                }

                projectFile.setDisplayName(displayName);
            }

            if (!GenericValidator.isBlankOrNull(form.data.releaseType)) {
                if (!Validator.validateReleaseType(form.data.releaseType)) {
                    return ErrorMessage.PROJECT_FILE_INVALID_RELEASE_TYPE.respond();
                }

                projectFile.setReleaseType(form.data.releaseType);
            }

            if (!GenericValidator.isBlankOrNull(form.data.classifier)) {
                if (!Validator.validateReleaseType(form.data.classifier)) {
                    return ErrorMessage.PROJECT_FILE_INVALID_CLASSIFIER.respond();
                }

                projectFile.setClassifier(form.data.classifier);
            }


            if (!GenericValidator.isBlankOrNull(form.data.version)) {
                if (Confluencia.FILE.existsByProjectIdAndVersion(session, project.getId(), form.data.version)) {
                    return ErrorMessage.PROJECT_FILE_TAKEN_VERSION.respond();
                }

                projectFile.setVersion(form.data.version);
            }


            if (!GenericValidator.isBlankOrNull(form.data.changelog)) {
                projectFile.setChangelog(form.data.changelog);
            }

            try {
                if (form.data.gameVersions != null) {
                    List<GameVersionsEntity> gameVersionRecords = Validator.validateGameVersions(project.getGame(), form.data.gameVersions);
                    if (!gameVersionRecords.isEmpty()) {
                        for (GameVersionsEntity version : gameVersionRecords) {
                            ProjectFileGameVersionsEntity gameVersionsEntity = new ProjectFileGameVersionsEntity();
                            gameVersionsEntity.setProjectFile(projectFile);
                            gameVersionsEntity.setGameVersion(version);
                            projectFile.addGameVersion(gameVersionsEntity);
                        }
                    }
                }

                if (form.data.loaders != null) {
                    projectFile.getLoaders().clear();
                    List<ProjectTypeLoadersEntity> projectTypeLoaders = Validator.validateProjectTypeLoaders(project.getProjectType(), form.data.loaders);
                    if (!projectTypeLoaders.isEmpty()) {
                        for (ProjectTypeLoadersEntity loader : projectTypeLoaders) {
                            ProjectFileLoadersEntity fileLoadersEntity = new ProjectFileLoadersEntity();
                            fileLoadersEntity.setProjectFile(projectFile);
                            fileLoadersEntity.setLoader(loader);
                            projectFile.addLoader(fileLoadersEntity);
                        }
                    }
                }

                if (form.data.dependencies != null) {
                    projectFile.getDependencies().clear();
                    List<ProjectFileDependenciesEntity> dependencyRecords = Validator.validateDependencies(session, project.getId(), form.data.dependencies);
                    if (!dependencyRecords.isEmpty()) {
                        for (ProjectFileDependenciesEntity dependency : dependencyRecords) {
                            dependency.setProjectFile(projectFile);
                            projectFile.addDependencies(dependency);
                        }
                    }
                }
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

            session.save(projectFile);

            return Response.noContent().build();
        });
    }

    @DELETE
    @Path("/files/{fileId}")
    public Response deleteProjectFile (@HeaderParam("Authorization") Token token, @PathParam("fileId") long fileId) {

        return Confluencia.getTransaction(session -> {
            final ProjectFilesEntity projectFile = Confluencia.FILE.findOneById(session, fileId);

            if (projectFile == null) {
                return ErrorMessage.NOT_FOUND_PROJECT_FILE.respond();
            }
            final ProjectsEntity project = projectFile.getProject();
            boolean authorized = ProjectPermissions.hasPermission(project, token, ProjectPermissions.FILE_DELETE);

            if (!authorized) {
                if (!projectFile.isReleased()) {
                    return ErrorMessage.NOT_FOUND_PROJECT_FILE.respond();
                }
                return ErrorMessage.USER_NOT_AUTHORIZED.respond();
            }

            session.delete(projectFile);

            return ResponseUtil.noContent();
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

                if (lastProject == null || lastProject.id != projectId) {
                    lastProject = projectFileMap.keySet().stream().filter(a -> a.id == projectId).findAny().orElse(null);
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
            projectFileMap.forEach((key, value) -> projectFilesPages.add(new DataSiteProjectFilesPage(key, value, value.size())));
            return ResponseUtil.successResponse(new DataProjectFileList(projectFilesPages));
        });
    }
}
