package com.diluv.api.endpoints.v1;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.GameTestDatabase;
import com.diluv.api.database.ProjectTestDatabase;
import com.diluv.api.database.UserTestDatabase;
import com.diluv.api.database.dao.GameDAO;
import com.diluv.api.database.dao.ProjectDAO;
import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.utils.HttpClientUtils;
import io.undertow.Undertow;

public class UserTest {

    private static final String IP = "0.0.0.0";
    private static final int PORT = 4567;

    @BeforeAll
    public static void setup () {

        GameDAO gameDAO = new GameTestDatabase();
        ProjectDAO projectDAO = new ProjectTestDatabase();
        UserDAO userDAO = new UserTestDatabase();

        Undertow server = Undertow.builder()
            .addHttpListener(PORT, IP)
            .setHandler(DiluvAPI.getHandler(gameDAO, projectDAO, userDAO))
            .build();
        server.start();
    }

    @Test
    public void testUser () throws IOException {

        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.disableAutomaticRetries();
        try (CloseableHttpClient httpClient = builder.build()) {
            runTest(httpClient, "/v1/user/me", "{\"message\":\"Invalid token\",\"error\":\"Unauthorized\"}");
            runTest(httpClient, "/v1/user/abc", "{\"message\":\"User not found\",\"error\":\"Bad Request\"}");
            runTest(httpClient, "/v1/user/testuser", "{\"data\":{\"username\":\"testuser\",\"avatarUrl\":\"https://via.placeholder.com/150\"}}");
        }
    }

    private void runTest (final CloseableHttpClient client, final String queryString, final String expected) throws IOException {

        Assertions.assertEquals(expected, HttpClientUtils.readResponse(client.execute(new HttpGet("http://" + IP + ":" + PORT + queryString))));
    }
}