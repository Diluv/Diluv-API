package com.diluv.api;

import com.diluv.api.endpoints.v1.auth.AuthAPI;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.cors.CorsHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DiluvAPI {
    private static final Logger LOGGER = Logger.getLogger(DiluvAPI.class.getName());

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static Connection connection;

    public static void main(String[] args) {
        new DiluvAPI().start();
    }

    private void start() {
        migrate();
        Undertow server = Undertow.builder()
                .addHttpListener(4567, "0.0.0.0")
                .setHandler(this.getHandler())
                .build();
        server.start();

        LOGGER.info("Server starting");
    }

    private void migrate() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(Constants.DB_HOSTNAME);
        ds.setUsername(Constants.DB_USERNAME);
        ds.setPassword(Constants.DB_PASSWORD);
        Flyway flyway = Flyway.configure().dataSource(ds).load();
        flyway.migrate();

        try {
            connection = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the routes and paths for requests
     *
     * @return HttpHandler for all the routes, with cors.
     */
    public HttpHandler getHandler() {
        RoutingHandler routing = Handlers.routing();
        routing.addAll(new AuthAPI());
        return new ErrorHandler(new BlockingHandler(new CorsHandler(routing)));
    }

    public static Connection connection() {
        return connection;
    }
}
