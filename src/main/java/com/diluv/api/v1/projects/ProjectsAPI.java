package com.diluv.api.v1.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.Query;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataGameVersion;
import com.diluv.api.data.DataProjectFileList;
import com.diluv.api.data.site.DataSiteProjectFileDisplay;
import com.diluv.api.data.site.DataSiteProjectFilesPage;
import com.diluv.api.provider.ResponseException;
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
import com.diluv.confluencia.database.record.ProjectFileGameVersionsEntity;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.sort.ProjectFileSort;
import com.diluv.confluencia.database.sort.Sort;

import static com.diluv.api.v1.games.GamesAPI.PROJECT_FILE_SORTS;

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

    @POST
    @Path("/{id}/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postProjectFile (@RequireToken @HeaderParam("Authorization") Token token, @PathParam("id") Long projectId, @Valid @MultipartForm ProjectFileUploadForm form) {

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

    @POST
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postProjectFile (@RequireToken @HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug, @MultipartForm ProjectFileUploadForm form) {

        return Confluencia.getTransaction(session -> {
            try {
                ProjectsEntity project = ProjectService.getProject(session, gameSlug, projectTypeSlug, projectSlug, token);
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
            projectFileMap.forEach((key, value) -> projectFilesPages.add(new DataSiteProjectFilesPage(key, value, value.size(), PROJECT_FILE_SORTS)));
            return ResponseUtil.successResponse(new DataProjectFileList(projectFilesPages));
        });
    }
}
