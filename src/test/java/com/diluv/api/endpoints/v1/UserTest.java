package com.diluv.api.endpoints.v1;

import java.io.IOException;
import java.util.Calendar;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.endpoints.v1.domain.ErrorDomain;
import com.diluv.api.endpoints.v1.user.domain.AuthorizedUserDomain;
import com.diluv.api.endpoints.v1.user.domain.ProjectDomain;
import com.diluv.api.endpoints.v1.user.domain.UserDomain;
import com.diluv.api.utils.FileReader;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.auth.JWTUtil;
import com.nimbusds.jose.JOSEException;

public class UserTest {
    private static final String BASE_URL = "/v1/users";

    private static String darkhaxToken;
    private static String jaredlll08Token;
    private static String invalidToken;

    @BeforeAll
    public static void setup () throws JOSEException {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        darkhaxToken = JWTUtil.generateAccessToken(0, "darkhax", calendar.getTime());
        jaredlll08Token = JWTUtil.generateAccessToken(1, "jaredlll08", calendar.getTime());

        invalidToken = "broken token";

        TestUtil.start();
    }

    @AfterAll
    public static void stop () {

        TestUtil.stop();
    }

    @Test
    public void testUserByUsername () throws IOException {

        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.disableAutomaticRetries();
        try (CloseableHttpClient httpClient = builder.build()) {
            // /user/me tests
            TestUtil.getTest(httpClient, BASE_URL + "/me", FileReader.readJsonFile("errors/invalid_token", ErrorDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/me", darkhaxToken, FileReader.readJsonFileByType("authorized_users/darkhax", AuthorizedUserDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/me", jaredlll08Token, FileReader.readJsonFileByType("authorized_users/jaredlll08", AuthorizedUserDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/me", invalidToken, FileReader.readJsonFile("errors/invalid_token", ErrorDomain.class));

            // Check for a non-existing user
            TestUtil.getTest(httpClient, BASE_URL + "/abc", FileReader.readJsonFile("errors/user_missing", ErrorDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/abc", darkhaxToken, FileReader.readJsonFile("errors/user_missing", ErrorDomain.class));

            // Check for existing user with and without a token, and an invalid token
            TestUtil.getTest(httpClient, BASE_URL + "/darkhax", FileReader.readJsonFileByType("users/darkhax", UserDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/darkhax", darkhaxToken, FileReader.readJsonFileByType("authorized_users/darkhax", AuthorizedUserDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/darkhax", jaredlll08Token, FileReader.readJsonFileByType("users/darkhax", UserDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/darkhax", invalidToken, FileReader.readJsonFile("errors/invalid_token", ErrorDomain.class));

            // Check for wrong authorization
            TestUtil.getTestToken(httpClient, BASE_URL + "/jaredlll08", darkhaxToken, FileReader.readJsonFileByType("users/jaredlll08", UserDomain.class));
        }
    }

    @Test
    public void testProjectsByUsername () throws IOException {

        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.disableAutomaticRetries();
        try (CloseableHttpClient httpClient = builder.build()) {
            // /user/me/projects tests
            TestUtil.getTest(httpClient, BASE_URL + "/me/projects", FileReader.readJsonFile("errors/invalid_token", ErrorDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/me/projects", invalidToken, FileReader.readJsonFile("errors/invalid_token", ErrorDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/me/projects", darkhaxToken, FileReader.readJsonFileByListType("user_projects/darkhax", ProjectDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/me/projects", jaredlll08Token, FileReader.readJsonFileByListType("user_projects/jaredlll08", ProjectDomain.class));

            // Check for a non-existing user
            TestUtil.getTest(httpClient, BASE_URL + "/abc/projects", FileReader.readJsonFile("errors/user_missing", ErrorDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/abc/projects", darkhaxToken, FileReader.readJsonFile("errors/user_missing", ErrorDomain.class));

            // Check for existing user with and without a token, and an invalid token
            TestUtil.getTest(httpClient, BASE_URL + "/darkhax/projects", FileReader.readJsonFileByListType("user_projects/darkhax", ProjectDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/darkhax/projects", darkhaxToken, FileReader.readJsonFileByListType("user_projects/darkhax", ProjectDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/darkhax/projects", jaredlll08Token, FileReader.readJsonFileByListType("user_projects/darkhax", ProjectDomain.class));
            TestUtil.getTestToken(httpClient, BASE_URL + "/darkhax/projects", invalidToken, FileReader.readJsonFile("errors/invalid_token", ErrorDomain.class));

            // Check for wrong authorization
            TestUtil.getTestToken(httpClient, BASE_URL + "/jaredlll08/projects", darkhaxToken, FileReader.readJsonFileByListType("user_projects/jaredlll08", ProjectDomain.class));
        }
    }

}