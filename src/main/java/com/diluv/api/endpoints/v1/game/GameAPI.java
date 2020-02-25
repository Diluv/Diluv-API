package com.diluv.api.endpoints.v1.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.diluv.api.aaa.RoutingHandlerPlus;
import com.diluv.api.endpoints.v1.IResponse;
import com.diluv.api.endpoints.v1.game.project.DataProject;
import com.diluv.api.endpoints.v1.game.project.DataProjectAuthorAuthorized;
import com.diluv.api.endpoints.v1.game.project.DataProjectAuthorized;
import com.diluv.api.endpoints.v1.game.project.DataProjectContributor;
import com.diluv.api.endpoints.v1.game.project.DataProjectFile;
import com.diluv.api.endpoints.v1.game.project.DataProjectFileAvailable;
import com.diluv.api.endpoints.v1.game.project.DataProjectFileInQueue;
import com.diluv.api.endpoints.v1.game.project.DataProjectType;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.dao.GameDAO;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.record.GameRecord;
import com.diluv.confluencia.database.record.ProjectAuthorRecord;
import com.diluv.confluencia.database.record.ProjectFileRecord;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.diluv.confluencia.database.record.ProjectTypeRecord;
import com.github.slugify.Slugify;
import io.undertow.server.HttpServerExchange;

public class GameAPI extends RoutingHandlerPlus {

    private final Slugify slugify = new Slugify();
    private final GameDAO gameDAO;
    private final ProjectDAO projectDAO;
    private final FileDAO fileDAO;

    public GameAPI (GameDAO gameDAO, ProjectDAO projectDAO, FileDAO fileDAO) {

        this.gameDAO = gameDAO;
        this.projectDAO = projectDAO;
        this.fileDAO = fileDAO;
        this.get("/", this::getGames);
        this.get("/{gameSlug}", "gameSlug", this::getGameBySlug);
        this.get("/{gameSlug}/types", "gameSlug", this::getProjectTypesByGameSlug);
        this.get("/{gameSlug}/{projectTypeSlug}", "gameSlug", "projectTypeSlug", this::getProjectTypesByGameSlugAndProjectType);
        this.get("/{gameSlug}/{projectTypeSlug}/projects", "gameSlug", "projectTypeSlug", this::getProjectsByGameSlugAndProjectType);
        this.get("/{gameSlug}/{projectTypeSlug}/{projectSlug}", "gameSlug", "projectTypeSlug", "projectSlug", this::getProjectByGameSlugAndProjectTypeAndProjectSlug);
        this.get("/{gameSlug}/{projectTypeSlug}/{projectSlug}/files", "gameSlug", "projectTypeSlug", "projectSlug", this::getProjectFilesByGameSlugAndProjectTypeAndProjectSlug);

//        this.post("/{game_slug}/{projectTypeSlug}", "gameSlug", "projectTypeSlug", this::postProjectByGameSlugAndProjectType);
//        this.post("/{game_slug}/{projectTypeSlug}/{project_slug}/files", "gameSlug", "projectTypeSlug", this::postProjectFilesByGameSlugAndProjectTypeAndProjectSlug);
    }

    private IResponse getGames (HttpServerExchange exchange) {

        final List<GameRecord> gameRecords = this.gameDAO.findAll();
        final List<DataGame> games = gameRecords.stream().map(DataGame::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, games);
    }

    private IResponse getGameBySlug (HttpServerExchange exchange, String gameSlug) {

        final GameRecord gameRecord = this.gameDAO.findOneBySlug(gameSlug);
        if (gameRecord == null) {

            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_GAME);
        }
        return ResponseUtil.successResponse(exchange, new DataGame(gameRecord));
    }

    private IResponse getProjectTypesByGameSlug (HttpServerExchange exchange, String gameSlug) {

        if (this.gameDAO.findOneBySlug(gameSlug) == null) {

            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_GAME);
        }

        final List<ProjectTypeRecord> projectTypesRecords = this.projectDAO.findAllProjectTypesByGameSlug(gameSlug);
        final List<DataProjectType> projectTypes = projectTypesRecords.stream().map(DataProjectType::new).collect(Collectors.toList());

        return ResponseUtil.successResponse(exchange, projectTypes);
    }

    private IResponse getProjectTypesByGameSlugAndProjectType (HttpServerExchange exchange, String gameSlug, String projectTypeSlug) {

        final ProjectTypeRecord projectTypesRecords = this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);

        if (projectTypesRecords == null) {

            if (this.gameDAO.findOneBySlug(gameSlug) == null) {

                return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_GAME);
            }

            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
        }

        return ResponseUtil.successResponse(exchange, new DataProjectType(projectTypesRecords));
    }

    private IResponse getProjectsByGameSlugAndProjectType (HttpServerExchange exchange, String gameSlug, String projectTypeSlug) {

        final List<ProjectRecord> projectRecords = this.projectDAO.findAllProjectsByGameSlugAndProjectType(gameSlug, projectTypeSlug);

        if (projectRecords.isEmpty()) {

            if (this.gameDAO.findOneBySlug(gameSlug) == null) {

                return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_GAME);
            }

            if (this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {

                return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
            }
        }

        final List<DataProject> projects = projectRecords.stream().map(DataProject::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }

    private IResponse getProjectByGameSlugAndProjectTypeAndProjectSlug (HttpServerExchange exchange, String gameSlug, String projectTypeSlug, String projectSlug) {

        final AccessToken token = JWTUtil.getTokenSafely(exchange);

        final ProjectRecord projectRecord = this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null || !projectRecord.isReleased() && token == null) {
            if (this.gameDAO.findOneBySlug(gameSlug) == null) {

                return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_GAME);
            }

            if (this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {

                return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
            }

            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_PROJECT);
        }

        final List<ProjectAuthorRecord> records = this.projectDAO.findAllProjectAuthorsByProjectId(projectRecord.getId());

        if (token != null) {

            List<String> permissions = null;
            if (token.getUserId() == projectRecord.getUserId()) {
                //TODO All permissions
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
                return ResponseUtil.successResponse(exchange, new DataProjectAuthorized(projectRecord, projectAuthors, permissions));
            }
        }

        final List<DataProjectContributor> projectAuthors = records.stream().map(DataProjectContributor::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, new DataProject(projectRecord, projectAuthors));
    }

    private IResponse getProjectFilesByGameSlugAndProjectTypeAndProjectSlug (HttpServerExchange exchange, String gameSlug, String projectTypeSlug, String projectSlug) {

        final AccessToken token = JWTUtil.getTokenSafely(exchange);

        final ProjectRecord projectRecord = this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null) {

            if (this.gameDAO.findOneBySlug(gameSlug) == null) {
                return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_GAME);
            }

            if (this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
                return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
            }

            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_PROJECT);
        }

        List<ProjectFileRecord> projectRecords;

        // TODO Check permissions
        if (token != null && projectRecord.getUserId() == token.getUserId()) {
            projectRecords = this.fileDAO.findAllByGameSlugAndProjectTypeAndProjectSlugAuthorized(gameSlug, projectTypeSlug, projectSlug);
        }
        else {
            projectRecords = this.fileDAO.findAllByGameSlugAndProjectTypeAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
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
        return ResponseUtil.successResponse(exchange, projects);
    }

//    private IResponse postProjectByGameSlugAndProjectType (HttpServerExchange exchange, String gameSlug, String projectTypeSlug) throws InvalidTokenException, MultiPartParserDefinition.FileTooLargeException {
//
//        final AccessToken token = JWTUtil.getToken(exchange);
//        if (token == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.USER_REQUIRED_TOKEN);
//        }
//
//        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_GAME);
//        }
//
//        if (this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
//        }
//
//        final FormData data = FormUtil.getMultiPartForm(exchange, 1000000L);
//        // Defaults to 1MB should be database stored. TODO
//
//        if (data == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.FORM_INVALID);
//        }
//        final String formName = RequestUtil.getFormParam(data, "name");
//        final String formSummary = RequestUtil.getFormParam(data, "summary");
//        final String formDescription = RequestUtil.getFormParam(data, "description");
//        final FormData.FileItem formLogo = RequestUtil.getFormFile(data, "logo");
//
//        if (!Validator.validateProjectName(formName)) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.PROJECT_INVALID_NAME);
//        }
//
//        if (!Validator.validateProjectSummary(formSummary)) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.PROJECT_INVALID_SUMMARY);
//        }
//
//        if (!Validator.validateProjectDescription(formDescription)) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.PROJECT_INVALID_DESCRIPTION);
//        }
//
//        if (formLogo == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.PROJECT_INVALID_LOGO);
//        }
//
//        FileUtil.getSize(formLogo);
//
//        final BufferedImage image = ImageUtil.isValidImage(formLogo);
//        if (image == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.PROJECT_INVALID_LOGO);
//        }
//        final String projectSlug = this.slugify.slugify(formName);
//
//        if (this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug) != null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.PROJECT_TAKEN_SLUG);
//        }
//        if (!this.projectDAO.insertProject(projectSlug, formName, formSummary, formDescription, token.getUserId(), gameSlug, projectTypeSlug)) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.FAILED_CREATE_PROJECT);
//        }
//
//        final ProjectRecord projectRecord = this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
//
//        if (projectRecord == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_PROJECT);
//        }
//
//        final File file = new File(Constants.CDN_FOLDER, String.format("games/%s/%s/%s/logo.png", gameSlug, projectTypeSlug, projectSlug));
//        if (!ImageUtil.saveImage(image, file)) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.ERROR_SAVING_IMAGE);
//        }
//        return ResponseUtil.successResponse(exchange, new DataProject(projectRecord));
//    }

//    private IResponse postProjectFilesByGameSlugAndProjectTypeAndProjectSlug (HttpServerExchange exchange) throws InvalidTokenException, MultiPartParserDefinition.FileTooLargeException {
//
//        final AccessToken token = JWTUtil.getToken(exchange);
//        if (token == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.USER_REQUIRED_TOKEN);
//        }
//
//        final String gameSlug = RequestUtil.getParam(exchange, "game_slug");
//        final String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
//        final String projectSlug = RequestUtil.getParam(exchange, "project_slug");
//
//        if (gameSlug == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.GAME_INVALID_SLUG);
//        }
//
//        if (projectTypeSlug == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.PROJECT_TYPE_INVALID_SLUG);
//        }
//
//        if (projectSlug == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.PROJECT_INVALID_SLUG);
//        }
//
//        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_GAME);
//        }
//
//        final ProjectTypeRecord projectTypeRecord = this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);
//        if (projectTypeRecord == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
//        }
//
//        final ProjectRecord projectRecord = this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
//        if (projectRecord == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
//        }
//
//        // TODO Needs to be moved to check the user permissions in the future
//        if (projectRecord.getUserId() != token.getUserId()) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.USER_NOT_AUTHORIZED);
//        }
//        final FormData data = FormUtil.getMultiPartForm(exchange, projectTypeRecord.getMaxSize());
//
//        if (data == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.FORM_INVALID);
//        }
//
//        final String formChangelog = RequestUtil.getFormParam(data, "changelog");
//        final FormData.FileItem formFile = RequestUtil.getFormFile(data, "file");
//
//        if (!Validator.validateProjectFileChangelog(formChangelog)) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.PROJECT_FILE_INVALID_CHANGELOG);
//        }
//
//        if (formFile == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.PROJECT_FILE_INVALID_FILE);
//        }
//
//        final Long fileSize = FileUtil.getSize(formFile);
//
//        final String sha512 = FileUtil.getSHA512(formFile);
//        final String fileName = formFile.getFile().getFileName().toString();
//
//        if (sha512 == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.FAILED_SHA512);
//        }
//
//        final Long id = this.fileDAO.insertProjectFile(fileName, fileSize, formChangelog, sha512, projectRecord.getId(), token.getUserId());
//        if (id == null) {
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.FAILED_CREATE_PROJECT_FILE);
//        }
//
//        final File file = new File(Constants.PROCESSING_FOLDER, String.format("%s/%s/%s/%s/%s", gameSlug, projectTypeSlug, projectSlug, id, fileName));
//        try {
//
//            FileUtils.copyInputStreamToFile(formFile.getInputStream(), file);
//        }
//        catch (final IOException e) {
//            DiluvAPI.LOGGER.error("Failed to postProjectFilesByGameSlugAndProjectTypeAndProjectSlug.", e);
//            return ResponseUtil.errorResponse(exchange, ErrorMessage.ERROR_WRITING);
//        }
//
//        final ProjectFileRecord record = this.fileDAO.findOneProjectFileQueueByFileId(id);
//        return ResponseUtil.successResponse(exchange, new DataProjectFileInQueue(record, gameSlug, projectTypeSlug, projectSlug));
//    }
}