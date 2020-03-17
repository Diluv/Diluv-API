package com.diluv.api.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;

import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.diluv.api.Database;
import com.diluv.api.DiluvAPIServer;
import com.diluv.api.Main;
import com.diluv.api.utils.auth.tokens.APIAccessToken;
import com.diluv.api.utils.auth.tokens.AccessToken;
import com.diluv.api.utils.auth.tokens.RefreshToken;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.FileDatabase;
import com.diluv.confluencia.database.GameDatabase;
import com.diluv.confluencia.database.NewsDatabase;
import com.diluv.confluencia.database.ProjectDatabase;
import com.diluv.confluencia.database.SecurityDatabase;
import com.diluv.confluencia.database.UserDatabase;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.dao.GameDAO;
import com.diluv.confluencia.database.dao.NewsDAO;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.dao.SecurityDAO;
import com.diluv.confluencia.database.dao.UserDAO;
import com.nimbusds.jose.JOSEException;
import io.restassured.RestAssured;

@Testcontainers
public class TestUtil {

    static final MariaDBContainer CONTAINER;

    static {
        CONTAINER = new MariaDBContainer<>();
        CONTAINER.start();
    }

    public static final String IP = "0.0.0.0";
    public static final int PORT = 4545;
    public static boolean running = false;
    public static final SecurityDAO SECURITY = new SecurityDatabase();
    public static final FileDAO FILE = new FileDatabase();
    public static final GameDAO GAME = new GameDatabase();
    public static final ProjectDAO PROJECT = new ProjectDatabase();
    public static final UserDAO USER = new UserDatabase();
    public static final NewsDAO NEWS = new NewsDatabase();

    public static String VALID_TOKEN;
    public static String VALID_TOKEN_TWO;
    public static String VALID_LONG_LASTING_TOKEN;
    public static String VALID_REFRESH_TOKEN;
    public static String INVALID_TOKEN = "invalid";

    static {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        try {
            VALID_TOKEN = new AccessToken(1, "darkhax", Collections.emptyList()).generate(calendar.getTime());
        }
        catch (JOSEException e) {
            e.printStackTrace();
        }

        try {
            VALID_TOKEN_TWO = new AccessToken(2, "jaredlll08", Collections.emptyList()).generate(calendar.getTime());
        }
        catch (JOSEException e) {
            e.printStackTrace();
        }

        try {
            VALID_REFRESH_TOKEN = new RefreshToken(1, "darkhax", "cd65cb00-b9a6-4da1-9b23-d7edfe2f9fa5").generate(calendar.getTime());
        }
        catch (JOSEException e) {
            e.printStackTrace();
        }

        calendar.add(Calendar.MONTH, 6);

        try {
            VALID_LONG_LASTING_TOKEN = new APIAccessToken(1, "darkhax", "4b3b85e3-f7ac-4c7b-b71a-df972909b213", Collections.singletonList(ProjectPermissions.FILE_UPLOAD.getName())).generate();
        }
        catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    public static void start () {

        if (!running) {
            Main.DATABASE = new Database(GAME, PROJECT, FILE, USER, SECURITY, NEWS);
            Confluencia.init(TestUtil.CONTAINER.getJdbcUrl(), TestUtil.CONTAINER.getUsername(), TestUtil.CONTAINER.getPassword(), true);
            DiluvAPIServer server = new DiluvAPIServer();
            server.start(IP, PORT);

            RestAssured.port = PORT;
            running = true;
        }
    }
}
