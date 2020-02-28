package com.diluv.api.v1.games;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.diluv.api.data.DataGame;
import com.diluv.api.data.DataProject;
import com.diluv.api.data.DataProjectAuthorAuthorized;
import com.diluv.api.data.DataProjectAuthorized;
import com.diluv.api.data.DataProjectContributor;
import com.diluv.api.data.DataProjectFile;
import com.diluv.api.data.DataProjectFileAvailable;
import com.diluv.api.data.DataProjectFileInQueue;
import com.diluv.api.data.DataProjectType;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.FileUtil;
import com.diluv.api.utils.ImageUtil;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.confluencia.database.record.GameRecord;
import com.diluv.confluencia.database.record.ProjectAuthorRecord;
import com.diluv.confluencia.database.record.ProjectFileRecord;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.diluv.confluencia.database.record.ProjectTypeRecord;
import com.github.slugify.Slugify;

import static com.diluv.api.Main.DATABASE;

@Path("/games")
public class GamesAPI {

    private final Slugify slugify = new Slugify();

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGames () {

        final List<GameRecord> gameRecords = DATABASE.gameDAO.findAll();
        final List<DataGame> games = gameRecords.stream().map(DataGame::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(games);
    }

    @GET
    @Path("/{gameSlug}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGame (@PathParam("gameSlug") String gameSlug) {

        final GameRecord gameRecord = DATABASE.gameDAO.findOneBySlug(gameSlug);
        if (gameRecord == null) {

            return ErrorMessage.NOT_FOUND_GAME.respond();
        }
        return ResponseUtil.successResponse(new DataGame(gameRecord));
    }

    @GET
    @Path("/{gameSlug}/types")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectTypes (@PathParam("gameSlug") String gameSlug) {

        if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {

            return ErrorMessage.NOT_FOUND_GAME.respond();
        }

        final List<ProjectTypeRecord> projectTypesRecords = DATABASE.projectDAO.findAllProjectTypesByGameSlug(gameSlug);
        final List<DataProjectType> projectTypes = projectTypesRecords.stream().map(DataProjectType::new).collect(Collectors.toList());

        return ResponseUtil.successResponse(projectTypes);
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectType (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug) {

        final ProjectTypeRecord projectTypesRecords = DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);

        if (projectTypesRecords == null) {

            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
        }

        return ResponseUtil.successResponse(new DataProjectType(projectTypesRecords));
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/projects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjects (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug) {

        final List<ProjectRecord> projectRecords = DATABASE.projectDAO.findAllProjectsByGameSlugAndProjectType(gameSlug, projectTypeSlug);

        if (projectRecords.isEmpty()) {

            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {

                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }
        }

        final List<DataProject> projects = projectRecords.stream().map(DataProject::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(projects);
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject (@HeaderParam("Authorization") AccessToken token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug) {

        final ProjectRecord projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null || !projectRecord.isReleased() && token == null) {
            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {

                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }

            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        final List<ProjectAuthorRecord> records = DATABASE.projectDAO.findAllProjectAuthorsByProjectId(projectRecord.getId());

        if (token != null) {

            List<String> permissions = null;
            if (token.getUserId() == projectRecord.getUserId()) {
                // TODO All permissions
                permissions = new ArrayList<>();
            }
            else {
                final Optional<ProjectAuthorRecord> record = records.stream().filter(par -> par.getUserId() == token.getUserId()).findFirst();

                if (record.isPresent()) {
                    permissions = record.get().getPermissions();
                }
            }

            if (permissions != null) {
                final List<DataProjectContributor> projectAuthors = records.stream().map(DataProjectAuthorAuthorized::new).collect(Collectors.toList());
                return ResponseUtil.successResponse(new DataProjectAuthorized(projectRecord, projectAuthors, permissions));
            }
        }

        final List<DataProjectContributor> projectAuthors = records.stream().map(DataProjectContributor::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(new DataProject(projectRecord, projectAuthors));
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}/files")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectFiles (@HeaderParam("Authorization") AccessToken token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug) {

        final ProjectRecord projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null) {

            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {
                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }

            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        List<ProjectFileRecord> projectRecords;

        // TODO Check permissions
        if (token != null && projectRecord.getUserId() == token.getUserId()) {
            projectRecords = DATABASE.fileDAO.findAllByGameSlugAndProjectTypeAndProjectSlugAuthorized(gameSlug, projectTypeSlug, projectSlug);
        }
        else {
            projectRecords = DATABASE.fileDAO.findAllByGameSlugAndProjectTypeAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        }
        final List<DataProjectFile> projects = new ArrayList<>();
        for (final ProjectFileRecord record : projectRecords) {
            if (record.getSha512() == null) {
                projects.add(new DataProjectFileInQueue(record, gameSlug, projectTypeSlug, projectSlug));
            }
            else {
                projects.add(new DataProjectFileAvailable(record, gameSlug, projectTypeSlug, projectSlug));
            }
        }
        return ResponseUtil.successResponse(projects);
    }

    @POST
    @Path("/{gameSlug}/{projectTypeSlug}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postProject (@HeaderParam("Authorization") AccessToken token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @MultipartForm ProjectCreateForm form) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {
            return ErrorMessage.NOT_FOUND_GAME.respond();
        }

        if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
            return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
        }
        // 1000000L
        // Defaults to 1MB should be database stored. TODO
        // final FormData.FileItem formLogo = RequestUtil.getFormFile(data, "logo");

        if (!Validator.validateProjectName(form.name)) {
            return ErrorMessage.PROJECT_INVALID_NAME.respond();
        }

        if (!Validator.validateProjectSummary(form.summary)) {
            return ErrorMessage.PROJECT_INVALID_SUMMARY.respond();
        }

        if (!Validator.validateProjectDescription(form.description)) {
            return ErrorMessage.PROJECT_INVALID_DESCRIPTION.respond();
        }

        if (form.logo == null) {
            return ErrorMessage.PROJECT_INVALID_LOGO.respond();
        }

        final BufferedImage image = ImageUtil.isValidImage(form.logo);

        if (image == null) {
            return ErrorMessage.PROJECT_INVALID_LOGO.respond();
        }
        final String projectSlug = this.slugify.slugify(form.name);

        if (DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug) != null) {
            return ErrorMessage.PROJECT_TAKEN_SLUG.respond();
        }
        if (!DATABASE.projectDAO.insertProject(projectSlug, form.name, form.summary, form.summary, token.getUserId(), gameSlug, projectTypeSlug)) {
            return ErrorMessage.FAILED_CREATE_PROJECT.respond();
        }

        final ProjectRecord projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);

        if (projectRecord == null) {
            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        final File file = new File(Constants.CDN_FOLDER, String.format("games/%s/%s/%s/logo.png", gameSlug, projectTypeSlug, projectSlug));
        if (!ImageUtil.saveImage(image, file)) {
            return ErrorMessage.ERROR_SAVING_IMAGE.respond();
        }
        return ResponseUtil.successResponse(new DataProject(projectRecord));
    }

    @POST
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postProjectFile (@HeaderParam("Authorization") AccessToken token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug, @MultipartForm ProjectFileUploadForm form) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        final ProjectRecord projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);

        if (projectRecord == null) {
            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {
                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }
            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        if (projectRecord.getUserId() != token.getUserId()) { // TODO make sure they have perms

            return ErrorMessage.USER_NOT_AUTHORIZED.respond();
        }

        if (!Validator.validateProjectFileChangelog(form.changelog)) {
            return ErrorMessage.PROJECT_FILE_INVALID_CHANGELOG.respond();
        }

        //TODO Check for form.fileName being null
        final String fileName = FilenameUtils.getName(form.fileName);
        final File tempFile = FileUtil.getTempFile(projectRecord.getId(), fileName);
        final String sha512 = FileUtil.writeFile(form.file, 25 * 1024 * 1024, tempFile);

        if (sha512 == null) {

            // TODO make this make sense
            return ErrorMessage.FAILED_SHA512.respond();
        }

        final Long id = DATABASE.fileDAO.insertProjectFile(fileName, tempFile.length(), form.changelog, sha512, projectRecord.getId(), token.getUserId());

        if (id == null) {

            return ErrorMessage.FAILED_CREATE_PROJECT_FILE.respond();
        }

        final boolean moved = tempFile.renameTo(FileUtil.getOutputLocation(gameSlug, projectTypeSlug, projectRecord.getId(), id, fileName));

        if (!moved) {

            return ErrorMessage.ERROR_WRITING.respond();
        }

        // TODO return more info
        return Response.ok().build();
    }
}
