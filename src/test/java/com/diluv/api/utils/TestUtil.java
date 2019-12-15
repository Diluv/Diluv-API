package com.diluv.api.utils;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Assertions;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.GameTestDatabase;
import com.diluv.api.database.ProjectTestDatabase;
import com.diluv.api.database.UserTestDatabase;
import com.diluv.api.database.dao.GameDAO;
import com.diluv.api.database.dao.ProjectDAO;
import com.diluv.api.database.dao.UserDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.undertow.Undertow;

public class TestUtil {

    public static final String IP = "0.0.0.0";
    public static final int PORT = 4567;
    private static Undertow server;

    public static void start () throws JsonProcessingException {

        GameDAO gameDAO = new GameTestDatabase();
        ProjectDAO projectDAO = new ProjectTestDatabase();
        UserDAO userDAO = new UserTestDatabase();

        TestUtil.server = Undertow.builder()
            .addHttpListener(PORT, IP)
            .setHandler(DiluvAPI.getHandler(gameDAO, projectDAO, userDAO))
            .build();
        TestUtil.server.start();
    }

    public static void stop () {

        TestUtil.server.stop();
    }

    public static void runTest (final CloseableHttpClient client, final String queryString, final String expected) throws IOException {

        runTokenTest(client, queryString, null, expected);
    }

    public static void runTokenTest (final CloseableHttpClient client, final String queryString, final String token, final String expected) throws IOException {

        HttpGet httpGet = new HttpGet("http://" + TestUtil.IP + ":" + TestUtil.PORT + queryString);
        if (token != null) {
            httpGet.setHeader("Authorization", "Bearer " + token);
        }
        Assertions.assertEquals(expected, HttpClientUtils.readResponse(client.execute(httpGet)));
    }
}
