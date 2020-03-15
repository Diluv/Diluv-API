package com.diluv.api.utils;

import java.util.Calendar;
import java.util.Collections;

import com.diluv.api.Database;
import com.diluv.api.DiluvAPIServer;
import com.diluv.api.Main;
import com.diluv.api.database.SecurityTestDatabase;
import com.diluv.api.database.FileTestDatabase;
import com.diluv.api.database.GameTestDatabase;
import com.diluv.api.database.NewsTestDatabase;
import com.diluv.api.database.ProjectTestDatabase;
import com.diluv.api.database.UserTestDatabase;
import com.diluv.api.utils.auth.tokens.APIAccessToken;
import com.diluv.api.utils.auth.tokens.AccessToken;
import com.diluv.api.utils.auth.tokens.RefreshToken;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.confluencia.database.dao.SecurityDAO;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.dao.GameDAO;
import com.diluv.confluencia.database.dao.NewsDAO;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.dao.UserDAO;
import com.nimbusds.jose.JOSEException;
import io.restassured.RestAssured;

public class TestUtil {

    public static final String IP = "0.0.0.0";
    public static final int PORT = 4545;
    public static boolean running = false;
    public static final GameDAO GAME_DAO = new GameTestDatabase();
    public static final ProjectDAO PROJECT_DAO = new ProjectTestDatabase();
    public static final FileDAO FILE_DAO = new FileTestDatabase();
    public static final UserDAO USER_DAO = new UserTestDatabase();
    public static final SecurityDAO SECURITY_DAO = new SecurityTestDatabase();
    public static final NewsDAO NEWS_DAO = new NewsTestDatabase();

    public static String VALID_TOKEN;
    public static String VALID_TOKEN_TWO;
    public static String VALID_LONG_LASTING_TOKEN;
    public static String VALID_REFRESH_TOKEN;
    public static String INVALID_TOKEN = "invalid";

    static {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        try {
            VALID_TOKEN = new AccessToken(0, "darkhax").generate(calendar.getTime());
        }
        catch (JOSEException e) {
            e.printStackTrace();
        }

        try {
            VALID_TOKEN_TWO = new AccessToken(1, "jaredlll08").generate(calendar.getTime());
        }
        catch (JOSEException e) {
            e.printStackTrace();
        }

        try {
            VALID_REFRESH_TOKEN = new RefreshToken(0, "darkhax", "cd65cb00-b9a6-4da1-9b23-d7edfe2f9fa5").generate(calendar.getTime());
        }
        catch (JOSEException e) {
            e.printStackTrace();
        }

        calendar.add(Calendar.MONTH, 6);

        try {
            VALID_LONG_LASTING_TOKEN = new APIAccessToken(0, "darkhax", "4b3b85e3-f7ac-4c7b-b71a-df972909b213", Collections.singletonList(ProjectPermissions.FILE_UPLOAD.getName())).generate();
        }
        catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    public static void start () {

        if (!running) {
            Main.DATABASE = new Database(GAME_DAO, PROJECT_DAO, FILE_DAO, USER_DAO, SECURITY_DAO, NEWS_DAO);
            DiluvAPIServer server = new DiluvAPIServer();
            server.start(IP, PORT);

            RestAssured.port = PORT;
            running = true;
        }
    }
}
