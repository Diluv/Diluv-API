package com.diluv.api.endpoints.v1;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.KeyRepresentation;

public class UserTest {
    private static final String URL = "/v1/users";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void getSelf () {

        Request.getError(URL + "/self", 401, ErrorMessage.USER_INVALID_TOKEN);
        Request.getErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/self", 401, ErrorMessage.USER_INVALID_TOKEN);

        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", "schema/user-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_JARED, URL + "/self", "schema/user-schema.json");
    }

    @Test
    public void patchSelf () {

        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("password", "invalid");
        multiPart.put("displayName", "ABC");
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", multiPart, 400, ErrorMessage.USER_INVALID_PASSWORD);

        multiPart.put("password", "password");
        multiPart.put("displayName", "Darkhax");
        multiPart.put("newPassword", "password1");
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", multiPart);
    }

    @Test
    public void patchSelfMFA () {

        final String secret = new Base64().encodeToString("12345678".getBytes());

        final GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
            .setNumberOfScratchCodes(8)
            .setSecretBits(160)
            .setCodeDigits(8)
            .setKeyRepresentation(KeyRepresentation.BASE64)
            .build();
        final GoogleAuthenticator gAuth = new GoogleAuthenticator(config);

        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("password", "invalid");
        multiPart.put("mfaStatus", "enable");
        multiPart.put("mfa", gAuth.getTotpPassword(secret));
        multiPart.put("mfaSecret", secret);

        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self/mfa", multiPart, 400, ErrorMessage.USER_INVALID_PASSWORD);

        multiPart.put("password", "password");
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self/mfa", multiPart, null);
    }

    @Test
    public void getUser () {

        // Check for a non-existing user
        Request.getError(URL + "/abc", 400, ErrorMessage.NOT_FOUND_USER);
        Request.getErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/abc", 400, ErrorMessage.NOT_FOUND_USER);

        // Check for existing user with and without a token, and an invalid token
        Request.getOk(URL + "/darkhax", "schema/user-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/darkhax", "schema/user-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_JARED, URL + "/darkhax", "schema/user-schema.json");

        // Check for wrong authorization
        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/jaredlll08", "schema/user-schema.json");
    }

    @Test
    public void getProjectsByUsername () {

        // Check for a non-existing user
        Request.getError(URL + "/abc/projects", 400, ErrorMessage.NOT_FOUND_USER);
        Request.getErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/abc/projects", 400, ErrorMessage.NOT_FOUND_USER);

        // Check for existing user with and without a token, and an invalid token
        Request.getOk(URL + "/darkhax/projects", "schema/project-list-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/darkhax/projects", "schema/project-list-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_JARED, URL + "/darkhax/projects", "schema/project-list-schema.json");
//        Request.getErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/darkhax/projects", 401, ErrorMessage.USER_INVALID_TOKEN);
    }
}