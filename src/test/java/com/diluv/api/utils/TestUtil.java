package com.diluv.api.utils;

import com.diluv.api.Database;
import com.diluv.api.DiluvAPIServer;
import com.diluv.api.Main;
import com.diluv.api.database.EmailTestDatabase;
import com.diluv.api.database.FileTestDatabase;
import com.diluv.api.database.GameTestDatabase;
import com.diluv.api.database.NewsTestDatabase;
import com.diluv.api.database.ProjectTestDatabase;
import com.diluv.api.database.UserTestDatabase;
import com.diluv.confluencia.database.dao.EmailDAO;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.dao.GameDAO;
import com.diluv.confluencia.database.dao.NewsDAO;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.dao.UserDAO;
import io.restassured.RestAssured;

public class TestUtil {

    public static final String IP = "0.0.0.0";
    public static final int PORT = 4545;
    public static boolean running = false;
    public static final GameDAO GAME_DAO = new GameTestDatabase();
    public static final ProjectDAO PROJECT_DAO = new ProjectTestDatabase();
    public static final FileDAO FILE_DAO = new FileTestDatabase();
    public static final UserDAO USER_DAO = new UserTestDatabase();
    public static final EmailDAO EMAIL_DAO = new EmailTestDatabase();
    public static final NewsDAO NEWS_DAO = new NewsTestDatabase();

    public static void start () {

        if (!running) {
            Main.DATABASE = new Database(GAME_DAO, PROJECT_DAO, FILE_DAO, USER_DAO, EMAIL_DAO, NEWS_DAO);
            DiluvAPIServer server = new DiluvAPIServer();
            server.start(IP, PORT);
//        TestUtil.server.start();

            RestAssured.port = PORT;
            running = true;
        }
    }

    public static void stop () {

//        TestUtil.server.stop();
    }
}
