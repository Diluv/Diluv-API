package com.diluv.api.endpoints.v1.game;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.database.dao.GameDAO;
import com.diluv.api.database.dao.ProjectDAO;
import com.diluv.api.database.record.GameRecord;
import com.diluv.api.database.record.ProjectFileRecord;
import com.diluv.api.database.record.ProjectRecord;
import com.diluv.api.database.record.ProjectTypeRecord;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.endpoints.v1.game.domain.GameDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectFileDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectTypeDomain;
import com.diluv.api.endpoints.v1.user.domain.ProjectDomain;
import com.diluv.api.utils.RequestUtil;
import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.error.ErrorResponse;
import com.github.slugify.Slugify;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;

public class GameAPI extends RoutingHandler {

    private final GameDAO gameDAO;
    private final ProjectDAO projectDAO;
    private final Slugify slugify = new Slugify();

    public GameAPI (GameDAO gameDAO, ProjectDAO projectDAO) {

        this.gameDAO = gameDAO;
        this.projectDAO = projectDAO;
        this.get("/v1/games", this::getGames);
        this.get("/v1/games/{game_slug}", this::getGameBySlug);
        this.get("/v1/games/{game_slug}/types", this::getProjectTypesByGameSlug);
        this.get("/v1/games/{game_slug}/{project_type_slug}", this::getProjectTypesByGameSlugAndProjectType);
        this.get("/v1/games/{game_slug}/{project_type_slug}/projects", this::getProjectsByGameSlugAndProjectType);
        this.get("/v1/games/{game_slug}/{project_type_slug}/{project_slug}", this::getProjectByGameSlugAndProjectTypeAndProjectSlug);
        this.get("/v1/games/{game_slug}/{project_type_slug}/{project_slug}/files", this::getProjectFilesByGameSlugAndProjectTypeAndProjectSlug);

        this.post("/v1/games/{game_slug}/{project_type_slug}", this::postProjectTypesByGameSlugAndProjectType);
    }

    private Domain getGames (HttpServerExchange exchange) {

        List<GameRecord> gameRecords = this.gameDAO.findAll();
        List<GameDomain> games = gameRecords.stream().map(GameDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, games);
    }

    private Domain getGameBySlug (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_GAME);
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
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_GAME);
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
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_GAME);
        }
        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PROJECT_TYPE);
        }

        if (this.gameDAO.findOneBySlug(gameSlug) == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_GAME);
        }

        ProjectTypeRecord projectTypesRecords = this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);

        if (projectTypesRecords == null) {
            // TODO Error
            return ResponseUtil.errorResponse(exchange, ErrorResponse.NOT_FOUND_PROJECT_TYPE);
        }

        return ResponseUtil.successResponse(exchange, new ProjectTypeDomain(projectTypesRecords));
    }

    private Domain getProjectsByGameSlugAndProjectType (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");

        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_GAME);
        }
        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PROJECT_TYPE);
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
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_GAME);
        }

        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PROJECT_TYPE);
        }

        if (projectSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PROJECT);
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
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_GAME);
        }

        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PROJECT_TYPE);
        }

        if (projectSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PROJECT);
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

        List<ProjectFileRecord> projectRecords = this.projectDAO.findAllProjectFilesByGameSlugAndProjectType(gameSlug, projectTypeSlug, projectSlug);
        List<ProjectFileDomain> projects = projectRecords.stream().map(ProjectFileDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }

    private Domain postProjectTypesByGameSlugAndProjectType (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");

        if (gameSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_GAME);
        }

        if (projectTypeSlug == null) {
            return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PROJECT_TYPE);
        }

        try (FormDataParser parser = FormParserFactory.builder().build().createParser(exchange)) {
            FormData data = parser.parseBlocking();
            String formName = RequestUtil.getFormParam(data, "name");
            String formSummary = RequestUtil.getFormParam(data, "summary");
            String formDescription = RequestUtil.getFormParam(data, "description");
            //TODO Logo

            if (Validator.validateProjectName(formName)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PROJECT_NAME);
            }

            if (Validator.validateProjectSummary(formSummary)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PROJECT_SUMMARY);
            }

            if (Validator.validateProjectDescription(formDescription)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_PROJECT_DESCRIPTION);
            }

            String token = RequestUtil.getToken(exchange);
            if (token == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_TOKEN);
            }
            Long authId = RequestUtil.getUserIdFromToken(token);
            if (authId == null) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.INVALID_TOKEN);
            }

            String slug = slugify.slugify(formName);

            if (this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, slug) != null) {
                //TODO Do we generate a new slug or just ask them to change project name?
                return ResponseUtil.errorResponse(exchange, ErrorResponse.TAKEN_SLUG);
            }
            //TODO Fix logo stuff

            if (!this.projectDAO.insertProject(slug, formName, formSummary, formDescription, "logo.png", authId, gameSlug, projectTypeSlug)) {
                return ResponseUtil.errorResponse(exchange, ErrorResponse.FAILED_CREATE_PROJECT);
            }
            //TODO Response project stuff
            return ResponseUtil.successResponse(exchange, null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // TODO Error
        return ResponseUtil.errorResponse(exchange, ErrorResponse.INTERNAL_SERVER_ERROR);
    }
}