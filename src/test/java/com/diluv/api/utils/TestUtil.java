package com.diluv.api.utils;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.EmailTestDatabase;
import com.diluv.api.database.GameTestDatabase;
import com.diluv.api.database.ProjectTestDatabase;
import com.diluv.api.database.UserTestDatabase;
import com.diluv.api.database.dao.EmailDAO;
import com.diluv.api.database.dao.GameDAO;
import com.diluv.api.database.dao.ProjectDAO;
import com.diluv.api.database.dao.UserDAO;
import io.restassured.RestAssured;
import io.undertow.Undertow;

public class TestUtil {

    public static final String IP = "0.0.0.0";
    public static final int PORT = 4567;
    private static Undertow server;

    public static void start () {

        GameDAO gameDAO = new GameTestDatabase();
        ProjectDAO projectDAO = new ProjectTestDatabase();
        UserDAO userDAO = new UserTestDatabase();
        EmailDAO emailDAO = new EmailTestDatabase();

        TestUtil.server = Undertow.builder()
            .addHttpListener(PORT, IP)
            .setHandler(DiluvAPI.getHandler(gameDAO, projectDAO, userDAO, emailDAO))
            .build();
        TestUtil.server.start();

        RestAssured.port = 4567;
    }

    public static void stop () {

        TestUtil.server.stop();
    }
}
