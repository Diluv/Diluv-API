package com.diluv.api.endpoints.v1.game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.endpoints.v1.game.domain.ProjectFileQueueDomain;

import org.apache.commons.io.FileUtils;

import com.diluv.api.DiluvAPI;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.endpoints.v1.game.domain.GameDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectFileDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectTypeDomain;
import com.diluv.api.endpoints.v1.user.domain.ProjectDomain;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.ImageUtil;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.error.ErrorResponse;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.dao.GameDAO;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.record.GameRecord;
import com.diluv.confluencia.database.record.ProjectFileQueueRecord;
import com.diluv.confluencia.database.record.ProjectFileRecord;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.diluv.confluencia.database.record.ProjectTypeRecord;
import com.github.slugify.Slugify;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;

public class GameAPI extends RoutingHandler {

    private final Slugify slugify = new Slugify();
    private final GameDAO gameDAO;
    private final ProjectDAO projectDAO;
    private final FileDAO fileDAO;

    public GameAPI (GameDAO gameDAO, ProjectDAO projectDAO, FileDAO fileDAO) {

        this.gameDAO = gameDAO;
        this.projectDAO = projectDAO;
        this.fileDAO = fileDAO;
        this.get("/", this::getGames);
        this.get("/{game_slug}", this::getGameBySlug);
        this.get("/{game_slug}/types", this::getProjectTypesByGameSlug);
        this.get("/{game_slug}/{project_type_slug}", this::getProjectTypesByGameSlugAndProjectType);
        this.get("/{game_slug}/{project_type_slug}/projects", this::getProjectsByGameSlugAndProjectType);
        this.get("/{game_slug}/{project_type_slug}/{project_slug}", this::getProjectByGameSlugAndProjectTypeAndProjectSlug);
        this.get("/{game_slug}/{project_type_slug}/{project_slug}/files", this::getProjectFilesByGameSlugAndProjectTypeAndProjectSlug);

        this.post("/{game_slug}/{project_type_slug}", this::postProjectByGameSlugAndProjectType);
        this.post("/{game_slug}/{project_type_slug}/{project_slug}/files", this::postProjectFilesByGameSlugAndProjectTypeAndProjectSlug);
    }

    private Domain getGames (HttpServerExchange exchange) {

        List<GameRecord> gameRecords = this.gameDAO.findAll();
        List<GameDomain> games = gameRecords.stream().map(GameDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, games);
    }

    private Domain getGameBySlug (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }

        GameRecord gameRecord = this.gameDAO.findOneBySlug(gameSlug);
        if (gameRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }
        return ResponseUtil.successResponse(exchange, new GameDomain(gameRecord));
    }

    private Domain getProjectTypesByGameSlug (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }

        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }

        List<ProjectTypeRecord> projectTypesRecords = this.projectDAO.findAllProjectTypesByGameSlug(gameSlug);
        List<ProjectTypeDomain> projectTypes = projectTypesRecords.stream().map(ProjectTypeDomain::new).collect(Collectors.toList());

        return ResponseUtil.successResponse(exchange, projectTypes);
    }

    private Domain getProjectTypesByGameSlugAndProjectType (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");

        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }
        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TYPE_INVALID_SLUG);
        }

        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }

        ProjectTypeRecord projectTypesRecords = this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);

        if (projectTypesRecords == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }

        return ResponseUtil.successResponse(exchange, new ProjectTypeDomain(projectTypesRecords));
    }

    private Domain getProjectsByGameSlugAndProjectType (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");

        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }
        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TYPE_INVALID_SLUG);
        }

        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }

        if (this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }

        List<ProjectRecord> projectRecords = this.projectDAO.findAllProjectsByGameSlugAndProjectType(gameSlug, projectTypeSlug);
        List<ProjectDomain> projects = projectRecords.stream().map(ProjectDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }

    private Domain getProjectByGameSlugAndProjectTypeAndProjectSlug (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        String projectSlug = RequestUtil.getParam(exchange, "project_slug");

        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }

        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TYPE_INVALID_SLUG);
        }

        if (projectSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_SLUG);
        }

        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }

        if (this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }

        ProjectRecord projectRecord = this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);

        if (projectRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT);
        }
        return ResponseUtil.successResponse(exchange, new ProjectDomain(projectRecord));
    }

    private Domain getProjectFilesByGameSlugAndProjectTypeAndProjectSlug (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        String projectSlug = RequestUtil.getParam(exchange, "project_slug");

        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }

        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TYPE_INVALID_SLUG);
        }

        if (projectSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_SLUG);
        }

        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }

        if (this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }

        if (this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT);
        }

        List<ProjectFileRecord> projectRecords = this.fileDAO.findAllProjectFilesByGameSlugAndProjectType(gameSlug, projectTypeSlug, projectSlug);
        List<ProjectFileDomain> projects = projectRecords.stream().map((ProjectFileRecord rs) -> new ProjectFileDomain(rs, projectSlug, projectTypeSlug, gameSlug)).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }

    private Domain postProjectByGameSlugAndProjectType (HttpServerExchange exchange) {

        String token = JWTUtil.getToken(exchange);
        if (token == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_REQUIRED_TOKEN);
        }
        AccessToken accessToken = AccessToken.getToken(token);
        if (accessToken == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_TOKEN);
        }

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");

        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }

        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TYPE_INVALID_SLUG);
        }

        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }

        if (this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }

        try (FormDataParser parser = FormParserFactory.builder().build().createParser(exchange)) {
            FormData data = parser.parseBlocking();
            String formName = RequestUtil.getFormParam(data, "name");
            String formSummary = RequestUtil.getFormParam(data, "summary");
            String formDescription = RequestUtil.getFormParam(data, "description");
            FormData.FileItem formLogo = RequestUtil.getFormFile(data, "logo");

            if (!Validator.validateProjectName(formName)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_NAME);
            }

            if (!Validator.validateProjectSummary(formSummary)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_SUMMARY);
            }

            if (!Validator.validateProjectDescription(formDescription)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_DESCRIPTION);
            }

            if (formLogo == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_LOGO);
            }

            // Defaults to 1MB should be database stored
            if (formLogo.getFileSize() > 1000000L) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_LOGO_SIZE);
            }
            BufferedImage image = ImageUtil.isValidImage(formLogo.getInputStream());
            if (image == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_LOGO);
            }

            String projectSlug = slugify.slugify(formName);

            if (this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug) != null) {
                //TODO Do we generate a new slug or just ask them to change project name?
                return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TAKEN_SLUG);
            }
            if (!this.projectDAO.insertProject(projectSlug, formName, formSummary, formDescription, accessToken.getUserId(), gameSlug, projectTypeSlug)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_PROJECT);
            }

            ProjectRecord projectRecord = this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);

            if (projectRecord == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT);
            }

            File file = new File(Constants.CDN_FOLDER, String.format("games/%s/%s/%s/logo.png", gameSlug, projectTypeSlug, projectSlug));
            if (!ImageUtil.saveImage(image, file)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.ERROR_SAVING_IMAGE);
            }
            return ResponseUtil.successResponse(exchange, new ProjectDomain(projectRecord));
        }
        catch (IOException e) {
            DiluvAPI.LOGGER.error("Failed to postProjectByGameSlugAndProjectType.", e);
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FORM_INVALID);
        }
    }

    private Domain postProjectFilesByGameSlugAndProjectTypeAndProjectSlug (HttpServerExchange exchange) {

        String token = JWTUtil.getToken(exchange);
        if (token == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_REQUIRED_TOKEN);
        }
        AccessToken accessToken = AccessToken.getToken(token);
        if (accessToken == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_INVALID_TOKEN);
        }

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        String projectSlug = RequestUtil.getParam(exchange, "project_slug");

        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.GAME_INVALID_SLUG);
        }

        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_TYPE_INVALID_SLUG);
        }

        if (projectSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_INVALID_SLUG);
        }

        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }

        ProjectTypeRecord projectTypeRecord = this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);
        if (projectTypeRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }

        ProjectRecord projectRecord = this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }

        //TODO Needs to be moved to check the user permissions in the future
        if (projectRecord.getUserId() != accessToken.getUserId()) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.USER_NOT_AUTHORIZED);
        }
        try (FormDataParser parser = FormParserFactory.builder().build().createParser(exchange)) {
            FormData data = parser.parseBlocking();
            String formChangelog = RequestUtil.getFormParam(data, "changelog");
            FormData.FileItem formFile = RequestUtil.getFormFile(data, "file");

            if (!Validator.validateProjectFileChangelog(formChangelog)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_FILE_INVALID_CHANGELOG);
            }

            if (formFile == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_FILE_INVALID_FILE);
            }

            if (formFile.getFileSize() > projectTypeRecord.getMaxSize()) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.PROJECT_FILE_INVALID_SIZE);
            }

            String fileName = formFile.getFile().getFileName().toString();
            Long id = this.fileDAO.insertProjectFileQueue(fileName, formFile.getFileSize(), formChangelog, projectRecord.getId(), accessToken.getUserId());
            if (id == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_PROJECT_FILE);
            }

            File file = new File(Constants.PROCESSING_FOLDER, String.format("%s/%s/%s/%s/%s", gameSlug, projectTypeSlug, projectSlug, id, fileName));
            FileUtils.copyInputStreamToFile(formFile.getInputStream(), file);

            ProjectFileQueueRecord record = this.fileDAO.findOneProjectFileQueueByFileId(id);
            return ResponseUtil.successResponse(exchange, new ProjectFileQueueDomain(record, projectSlug, projectTypeSlug, gameSlug));
        }
        catch (IOException e) {
            DiluvAPI.LOGGER.error("Failed to postProjectTypesByGameSlugAndProjectType.", e);
            return ResponseUtil.errorResponse(exchange, ErrorResponse.FORM_INVALID);
        }
    }
}