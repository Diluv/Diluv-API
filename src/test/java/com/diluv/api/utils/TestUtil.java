package com.diluv.api.utils;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.EmailTestDatabase;
import com.diluv.api.database.FileTestDatabase;
import com.diluv.api.database.GameTestDatabase;
import com.diluv.api.database.ProjectTestDatabase;
import com.diluv.api.database.UserTestDatabase;
import com.diluv.confluencia.database.dao.EmailDAO;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.dao.GameDAO;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.dao.UserDAO;
import io.restassured.RestAssured;
import io.undertow.Undertow;

public class TestUtil {

    public static final String IP = "0.0.0.0";
    public static final int PORT = 4567;
    private static Undertow server;

    public static final GameDAO GAME_DAO = new GameTestDatabase();
    public static final ProjectDAO PROJECT_DAO = new ProjectTestDatabase();
    public static final FileDAO FILE_DAO = new FileTestDatabase();
    public static final UserDAO USER_DAO = new UserTestDatabase();
    public static final EmailDAO EMAIL_DAO = new EmailTestDatabase();

    public static void start () {


        TestUtil.server = Undertow.builder()
            .addHttpListener(PORT, IP)
            .setHandler(DiluvAPI.getHandler(GAME_DAO, PROJECT_DAO, FILE_DAO, USER_DAO, EMAIL_DAO))
            .build();
        TestUtil.server.start();

        RestAssured.port = 4567;
    }

    public static void stop () {

        TestUtil.server.stop();
    }
}
