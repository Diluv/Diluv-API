package com.diluv.api.endpoints.v1.game;

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
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;

public class GameAPI extends RoutingHandler {

    private final GameDAO gameDAO;
    private final ProjectDAO projectDAO;

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
    }

    private Domain getGames (HttpServerExchange exchange) {

        List<GameRecord> gameRecords = this.gameDAO.findAll();
        List<GameDomain> games = gameRecords.stream().map(GameDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, games);
    }

    private Domain getGameBySlug (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        if (gameSlug == null) {
            // TODO Error, shouldn't happen, but it can
            return null;
        }

        GameRecord gameRecord = this.gameDAO.findOneBySlug(gameSlug);
        if (gameRecord == null) {
            // TODO Error, Database select error or a connection error, this should be logged as it could show a larger problem
            return null;
        }
        return ResponseUtil.successResponse(exchange, new GameDomain(gameRecord));
    }

    private Domain getProjectTypesByGameSlug (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        if (gameSlug == null) {
            // TODO Error, shouldn't happen, but it can
            return null;
        }

        List<ProjectTypeRecord> projectTypesRecords = this.projectDAO.findAllProjectTypesByGameSlug(gameSlug);
        List<ProjectTypeDomain> projectTypes = projectTypesRecords.stream().map(ProjectTypeDomain::new).collect(Collectors.toList());

        return ResponseUtil.successResponse(exchange, projectTypes);
    }

    private Domain getProjectTypesByGameSlugAndProjectType (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        if (gameSlug == null) {
            // TODO Error, shouldn't happen, but it can
            return null;
        }

        String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        if (projectTypeSlug == null) {
            // TODO Error, shouldn't happen, but it can
            return null;
        }

        ProjectTypeRecord projectTypesRecords = this.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);

        if (projectTypesRecords == null) {
            // TODO Error
            return null;
        }

        return ResponseUtil.successResponse(exchange, new ProjectTypeDomain(projectTypesRecords));
    }

    private Domain getProjectsByGameSlugAndProjectType (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        if (gameSlug == null) {
            // TODO Error, shouldn't happen, but it can
            return null;
        }
        String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        if (projectTypeSlug == null) {
            // TODO Error, shouldn't happen, but it can
            return null;
        }

        List<ProjectRecord> projectRecords = this.projectDAO.findAllProjectsByGameSlugAndProjectType(gameSlug, projectTypeSlug);
        List<ProjectDomain> projects = projectRecords.stream().map(ProjectDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }

    private Domain getProjectByGameSlugAndProjectTypeAndProjectSlug (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        if (gameSlug == null) {
            // TODO Error, shouldn't happen, but it can
            return null;
        }
        String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        if (projectTypeSlug == null) {
            // TODO Error, shouldn't happen, but it can
            return null;
        }

        String projectSlug = RequestUtil.getParam(exchange, "project_slug");
        if (projectSlug == null) {
            // TODO Error, shouldn't happen, but it can
            return null;
        }

        ProjectRecord projectRecord = this.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);

        if (projectRecord == null) {
            // TODO Error
            return null;
        }
        return ResponseUtil.successResponse(exchange, new ProjectDomain(projectRecord));
    }

    private Domain getProjectFilesByGameSlugAndProjectTypeAndProjectSlug (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        if (gameSlug == null) {
            // TODO Error, shouldn't happen, but it can
            return null;
        }
        String projectTypeSlug = RequestUtil.getParam(exchange, "project_type_slug");
        if (projectTypeSlug == null) {
            // TODO Error, shouldn't happen, but it can
            return null;
        }

        String projectSlug = RequestUtil.getParam(exchange, "project_slug");
        if (projectSlug == null) {
            // TODO Error, shouldn't happen, but it can
            return null;
        }

        List<ProjectFileRecord> projectRecords = this.projectDAO.findAllProjectFilesByGameSlugAndProjectType(gameSlug, projectTypeSlug, projectSlug);
        List<ProjectFileDomain> projects = projectRecords.stream().map(ProjectFileDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, projects);
    }
}