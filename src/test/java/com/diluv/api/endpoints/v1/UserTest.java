package com.diluv.api.endpoints.v1;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.ProjectTestDatabase;
import com.diluv.api.database.UserTestDatabase;
import com.diluv.api.utils.HttpClientUtils;
import io.undertow.Undertow;

public class UserTest {

    private static final String IP = "0.0.0.0";
    private static final int PORT = 4567;

    @BeforeClass
    public static void setup () {

        UserTestDatabase userDAO = new UserTestDatabase();
        ProjectTestDatabase projectDAO = new ProjectTestDatabase();

        Undertow server = Undertow.builder()
            .addHttpListener(PORT, IP)
            .setHandler(DiluvAPI.getHandler(userDAO, projectDAO))
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

        Assert.assertEquals(expected, HttpClientUtils.readResponse(client.execute(new HttpGet("http://" + IP + ":" + PORT + queryString))));
    }
}