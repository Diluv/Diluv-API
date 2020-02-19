package com.diluv.api;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.diluv.api.endpoints.v1.auth.AuthAPI;
import com.diluv.api.endpoints.v1.game.GameAPI;
import com.diluv.api.endpoints.v1.news.NewsAPI;
import com.diluv.api.endpoints.v1.user.UserAPI;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.cors.CorsHandler;
import com.diluv.api.utils.error.ErrorHandler;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.EmailDatabase;
import com.diluv.confluencia.database.FileDatabase;
import com.diluv.confluencia.database.GameDatabase;
import com.diluv.confluencia.database.NewsDatabase;
import com.diluv.confluencia.database.ProjectDatabase;
import com.diluv.confluencia.database.UserDatabase;
import com.diluv.confluencia.database.dao.EmailDAO;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.dao.GameDAO;
import com.diluv.confluencia.database.dao.NewsDAO;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.dao.UserDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;

public class DiluvAPI {

    public static final Logger LOGGER = LogManager.getLogger("API");
    public static final Gson GSON = new GsonBuilder().create();

    public static void main (String[] args) {

        new DiluvAPI().start("0.0.0.0", 4567);
    }

    private void start (String host, int port) {

        GameDAO gameDAO = new GameDatabase();
        ProjectDAO projectDAO = new ProjectDatabase();
        FileDAO fileDAO = new FileDatabase();
        UserDAO userDAO = new UserDatabase();
        EmailDAO emailDAO = new EmailDatabase();
        NewsDAO newsDAO = new NewsDatabase();

        migrate(emailDAO);
        Undertow server = Undertow.builder()
            .addHttpListener(port, host)
            .setHandler(DiluvAPI.getHandler(gameDAO, projectDAO, fileDAO, userDAO, emailDAO, newsDAO))
            .build();
        server.start();
        LOGGER.info("Server starting on {}:{}", host, port);
    }

    private void migrate (EmailDAO emailDAO) {

        // TODO CHANGE TO FALSE ON RELEASE
        Confluencia.init(Constants.DB_HOSTNAME, Constants.DB_USERNAME, Constants.DB_PASSWORD, true);
        this.blacklistDomains(emailDAO);
    }

    private void blacklistDomains (EmailDAO emailDAO) {

        try {
            URL uri = new URL("https://raw.githubusercontent.com/wesbos/burner-email-providers/master/emails.txt");
            String[] data = IOUtils.toString(uri, (Charset) null).toLowerCase().split("\n");
            emailDAO.insertDomainBlacklist(data);
        }
        catch (IOException e) {
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
    public static HttpHandler getHandler (GameDAO gameDAO, ProjectDAO projectDAO, FileDAO fileDAO, UserDAO userDAO, EmailDAO emailDAO, NewsDAO newsDAO) {

        PathHandler routing = Handlers.path();
        Path rootPath = Paths.get("public");
        PathHandler path = Handlers.path();
        path.addPrefixPath("/public", new ResourceHandler(new PathResourceManager(rootPath)).setDirectoryListingEnabled(true));
        routing.addPrefixPath("/auth", new AuthAPI(userDAO, emailDAO));
        routing.addPrefixPath("/users", new UserAPI(userDAO, projectDAO));
        routing.addPrefixPath("/games", new GameAPI(gameDAO, projectDAO, fileDAO));
        routing.addPrefixPath("/news", new NewsAPI(newsDAO));
        path.addPrefixPath("/v1", routing);
        return new BlockingHandler(new CorsHandler(new ErrorHandler(path)));
    }
}