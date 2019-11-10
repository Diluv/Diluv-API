package com.diluv.api.endpoints.v1.game;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.database.dao.GameDAO;
import com.diluv.api.database.dao.ProjectDAO;
import com.diluv.api.database.record.GameRecord;
import com.diluv.api.database.record.ProjectRecord;
import com.diluv.api.endpoints.v1.domain.Domain;
import com.diluv.api.endpoints.v1.game.domain.GameDomain;
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
        this.get("/v1/game", this::getGames);
        this.get("/v1/game/{game_slug}", this::getGameBySlug);
    }

    private Domain getGames (HttpServerExchange exchange) {

        List<GameRecord> gameRecords = this.gameDAO.findAll();
        List<GameDomain> games = gameRecords.stream().map(GameDomain::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(exchange, games);
    }

    private Domain getGameBySlug (HttpServerExchange exchange) {

        String gameSlug = RequestUtil.getParam(exchange, "game_slug");
        if (gameSlug == null) {
            // Error, shouldn't happen, but it can
            return null;
        }

        GameRecord gameRecord = this.gameDAO.findOneBySlug(gameSlug);
        if (gameRecord == null) {
            // TODO Error, Database select error or a connection error, this should be logged as it could show a larger problem
            return null;
        }
        return ResponseUtil.successResponse(exchange, new GameDomain(gameRecord));
    }

}