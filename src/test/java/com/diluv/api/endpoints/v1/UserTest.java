package com.diluv.api.endpoints.v1;

import java.io.IOException;
import java.util.Calendar;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.Constants;
import com.diluv.api.utils.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

public class UserTest {
    private static final String BASE_URL = "/v1/users";

    private static String darkhaxToken;
    private static String jaredlll08Token;
    private static String invalidToken;

    @BeforeAll
    public static void setup () throws JsonProcessingException {

//        final JwtGenerator<CommonProfile> jwtGenerator = new JwtGenerator<>(Constants.RSA_SIGNATURE_CONFIGURATION);
//
//        // Makes the access expire in 30 minutes
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MINUTE, 30);
//        jwtGenerator.setExpirationTime(calendar.getTime());
//
//        final CommonProfile validProfile = new CommonProfile();
//        validProfile.setId("darkhax");
//        validProfile.addAttribute(Pac4jConstants.USERNAME, "darkhax");
//        darkhaxToken = jwtGenerator.generate(validProfile);
//
//        final CommonProfile secondProfile = new CommonProfile();
//        secondProfile.setId("jaredlll08");
//        secondProfile.addAttribute(Pac4jConstants.USERNAME, "jaredlll08");
//        jaredlll08Token = jwtGenerator.generate(secondProfile);

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
            TestUtil.runTest(httpClient, BASE_URL + "/me", "{\"message\":\"Invalid token\",\"error\":\"Unauthorized\"}");
            TestUtil.runTokenTest(httpClient, BASE_URL + "/me", darkhaxToken, "{\"data\":{\"username\":\"darkhax\",\"avatarUrl\":\"https://via.placeholder.com/150\",\"email\":\"darkhax@example.com\",\"mfa\":true}}");
            TestUtil.runTokenTest(httpClient, BASE_URL + "/me", jaredlll08Token, "{\"data\":{\"username\":\"jaredlll08\",\"avatarUrl\":\"https://via.placeholder.com/150\",\"email\":\"jaredlll08@example.com\",\"mfa\":true}}");
            TestUtil.runTokenTest(httpClient, BASE_URL + "/me", invalidToken, "{\"message\":\"Invalid token\",\"error\":\"Unauthorized\"}");

            // Check for a non-existing user
            TestUtil.runTest(httpClient, BASE_URL + "/abc", "{\"message\":\"User not found\",\"error\":\"Bad Request\"}");
            TestUtil.runTokenTest(httpClient, BASE_URL + "/abc", darkhaxToken, "{\"message\":\"User not found\",\"error\":\"Bad Request\"}");

            // Check for existing user with and without a token, and an invalid token
            TestUtil.runTest(httpClient, BASE_URL + "/darkhax", "{\"data\":{\"username\":\"darkhax\",\"avatarUrl\":\"https://via.placeholder.com/150\"}}");
            TestUtil.runTokenTest(httpClient, BASE_URL + "/darkhax", darkhaxToken, "{\"data\":{\"username\":\"darkhax\",\"avatarUrl\":\"https://via.placeholder.com/150\",\"email\":\"darkhax@example.com\",\"mfa\":true}}");
            TestUtil.runTokenTest(httpClient, BASE_URL + "/darkhax", jaredlll08Token, "{\"data\":{\"username\":\"darkhax\",\"avatarUrl\":\"https://via.placeholder.com/150\"}}");
            // TODO Should invalid token error? or pretend there wasn't a token sent?
            TestUtil.runTokenTest(httpClient, BASE_URL + "/darkhax", invalidToken, "{\"data\":{\"username\":\"darkhax\",\"avatarUrl\":\"https://via.placeholder.com/150\"}}");

            // Check for wrong authorization
            TestUtil.runTokenTest(httpClient, BASE_URL + "/jaredlll08", darkhaxToken, "{\"data\":{\"username\":\"jaredlll08\",\"avatarUrl\":\"https://via.placeholder.com/150\"}}");
        }
    }

    @Test
    public void testProjectsByUsername () throws IOException {

        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.disableAutomaticRetries();
        try (CloseableHttpClient httpClient = builder.build()) {
            // /user/me/projects tests
            TestUtil.runTest(httpClient, BASE_URL + "/me/projects", "{\"message\":\"Invalid token\",\"error\":\"Unauthorized\"}");
            TestUtil.runTokenTest(httpClient, BASE_URL + "/me/projects", invalidToken, "{\"message\":\"Invalid token\",\"error\":\"Unauthorized\"}");
            TestUtil.runTokenTest(httpClient, BASE_URL + "/me/projects", darkhaxToken, "{\"data\":[{\"name\":\"Bookshelf\",\"slug\":\"bookshelf\",\"summary\":\"Bookshelf summary\",\"description\":\"Bookshelf description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":32923285,\"createdAt\":1573482394,\"updatedAt\":1573482394},{\"name\":\"Caliper\",\"slug\":\"caliper\",\"summary\":\"Caliper summary\",\"description\":\"Caliper description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":3176949,\"createdAt\":1573482589,\"updatedAt\":1573482589}]}");
            TestUtil.runTokenTest(httpClient, BASE_URL + "/me/projects", jaredlll08Token, "{\"data\":[{\"name\":\"CraftTweaker\",\"slug\":\"crafttweaker\",\"summary\":\"CraftTweaker summary\",\"description\":\"CraftTweaker description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":43825671,\"createdAt\":1573482589,\"updatedAt\":1573482589}]}");

            // Check for a non-existing user
            TestUtil.runTest(httpClient, BASE_URL + "/abc/projects", "{\"message\":\"User not found\",\"error\":\"Bad Request\"}");
            TestUtil.runTokenTest(httpClient, BASE_URL + "/abc/projects", darkhaxToken, "{\"message\":\"User not found\",\"error\":\"Bad Request\"}");

            // Check for existing user with and without a token, and an invalid token
            TestUtil.runTest(httpClient, BASE_URL + "/darkhax/projects", "{\"data\":[{\"name\":\"Bookshelf\",\"slug\":\"bookshelf\",\"summary\":\"Bookshelf summary\",\"description\":\"Bookshelf description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":32923285,\"createdAt\":1573482394,\"updatedAt\":1573482394},{\"name\":\"Caliper\",\"slug\":\"caliper\",\"summary\":\"Caliper summary\",\"description\":\"Caliper description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":3176949,\"createdAt\":1573482589,\"updatedAt\":1573482589}]}");
            TestUtil.runTokenTest(httpClient, BASE_URL + "/darkhax/projects", darkhaxToken, "{\"data\":[{\"name\":\"Bookshelf\",\"slug\":\"bookshelf\",\"summary\":\"Bookshelf summary\",\"description\":\"Bookshelf description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":32923285,\"createdAt\":1573482394,\"updatedAt\":1573482394},{\"name\":\"Caliper\",\"slug\":\"caliper\",\"summary\":\"Caliper summary\",\"description\":\"Caliper description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":3176949,\"createdAt\":1573482589,\"updatedAt\":1573482589}]}");
            TestUtil.runTokenTest(httpClient, BASE_URL + "/darkhax/projects", jaredlll08Token, "{\"data\":[{\"name\":\"Bookshelf\",\"slug\":\"bookshelf\",\"summary\":\"Bookshelf summary\",\"description\":\"Bookshelf description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":32923285,\"createdAt\":1573482394,\"updatedAt\":1573482394},{\"name\":\"Caliper\",\"slug\":\"caliper\",\"summary\":\"Caliper summary\",\"description\":\"Caliper description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":3176949,\"createdAt\":1573482589,\"updatedAt\":1573482589}]}");
            // TODO Should invalid token error? or pretend there wasn't a token sent?
            TestUtil.runTokenTest(httpClient, BASE_URL + "/darkhax/projects", invalidToken, "{\"data\":[{\"name\":\"Bookshelf\",\"slug\":\"bookshelf\",\"summary\":\"Bookshelf summary\",\"description\":\"Bookshelf description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":32923285,\"createdAt\":1573482394,\"updatedAt\":1573482394},{\"name\":\"Caliper\",\"slug\":\"caliper\",\"summary\":\"Caliper summary\",\"description\":\"Caliper description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":3176949,\"createdAt\":1573482589,\"updatedAt\":1573482589}]}");

            // Check for wrong authorization
            TestUtil.runTokenTest(httpClient, BASE_URL + "/jaredlll08/projects", darkhaxToken, "{\"data\":[{\"name\":\"CraftTweaker\",\"slug\":\"crafttweaker\",\"summary\":\"CraftTweaker summary\",\"description\":\"CraftTweaker description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":43825671,\"createdAt\":1573482589,\"updatedAt\":1573482589}]}");
        }
    }

}