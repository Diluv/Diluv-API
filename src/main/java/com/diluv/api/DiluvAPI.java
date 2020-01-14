package com.diluv.api;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.flywaydb.core.Flyway;

import com.diluv.api.database.EmailDatabase;
import com.diluv.api.database.GameDatabase;
import com.diluv.api.database.ProjectDatabase;
import com.diluv.api.database.UserDatabase;
import com.diluv.api.database.dao.EmailDAO;
import com.diluv.api.database.dao.GameDAO;
import com.diluv.api.database.dao.ProjectDAO;
import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.endpoints.v1.auth.AuthAPI;
import com.diluv.api.endpoints.v1.game.GameAPI;
import com.diluv.api.endpoints.v1.user.UserAPI;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.SQLHandler;
import com.diluv.api.utils.cors.CorsHandler;
import com.diluv.api.utils.error.ErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;
import com.zaxxer.hikari.HikariDataSource;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import org.apache.logging.log4j.Logger;

public class DiluvAPI {
	
	public static final Logger LOGGER = LogManager.getLogger("API");
	
    public static final ObjectMapper MAPPER = new ObjectMapper()
        .registerModule(new ParanamerModule());
    private static HikariDataSource ds;
    private static Connection connection;

    public static void main (String[] args) {

        new DiluvAPI().start("0.0.0.0", 4567);
    }

    private void start (String host, int port) {

        migrate();
        GameDAO gameDAO = new GameDatabase();
        ProjectDAO projectDAO = new ProjectDatabase();
        UserDAO userDAO = new UserDatabase();
        EmailDAO emailDAO = new EmailDatabase();

        Undertow server = Undertow.builder()
            .addHttpListener(port, host)
            .setHandler(DiluvAPI.getHandler(gameDAO, projectDAO, userDAO, emailDAO))
            .build();
        server.start();

        LOGGER.info("Server starting on {}:{}", host, port);
    }

    private void migrate () {

        ds = new HikariDataSource();
        ds.setJdbcUrl(Constants.DB_HOSTNAME);
        ds.setUsername(Constants.DB_USERNAME);
        ds.setPassword(Constants.DB_PASSWORD);
        ds.addDataSourceProperty("rewriteBatchedStatements", "true");
        Flyway flyway = Flyway.configure().dataSource(ds).load();
        flyway.clean(); // TODO remove after release
        flyway.migrate();

        this.blacklistDomains();
    }

    private void blacklistDomains () {

        String insertDomainBlacklist = SQLHandler.readFile("email/insertDomainBlacklist");
        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(insertDomainBlacklist)) {
            URL uri = new URL("https://raw.githubusercontent.com/wesbos/burner-email-providers/master/emails.txt");
            String[] data = IOUtils.toString(uri, (Charset) null).toLowerCase().split("\n");
            Set<String> domains = Arrays.stream(data).collect(Collectors.toSet());
            for (String domain : domains) {
                stmt.setString(1, domain);
                stmt.addBatch();
            }
            stmt.executeLargeBatch();
        }
        catch (IOException | SQLException e) {
        	LOGGER.error("Failed to insert domain blacklist.", e);
        }
    }

    /**
     * Gets the routes and paths for requests
     *
     * @param userDAO The User DAO to fetch user data
     * @param projectDAO The Project DAO to fetch project data
     * @return HttpHandler for all the routes, with cors.
     */
    public static HttpHandler getHandler (GameDAO gameDAO, ProjectDAO projectDAO, UserDAO userDAO, EmailDAO emailDAO) {

        RoutingHandler routing = Handlers.routing();
        routing.addAll(new AuthAPI(userDAO, emailDAO));
        routing.addAll(new UserAPI(userDAO, projectDAO));
        routing.addAll(new GameAPI(gameDAO, projectDAO));
        return new ErrorHandler(new BlockingHandler(new CorsHandler(routing)));
    }

    public static Connection connection () throws SQLException {

        if (connection == null || connection.isClosed())
            connection = ds.getConnection();
        return connection;
    }
}
