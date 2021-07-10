package com.diluv.api.endpoints.v1;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.v1.users.User2FA;
import com.diluv.api.v1.users.UserUpdate;
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

        Request.getError(URL + "/self", ErrorMessage.USER_REQUIRED_TOKEN);
        Request.getErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/self", ErrorMessage.USER_INVALID_TOKEN);

        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", "schema/user-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_JARED, URL + "/self", "schema/user-schema.json");
    }

    @Test
    public void patchSelf () {

        Map<String, Object> multiPart = new HashMap<>();

        UserUpdate data = new UserUpdate();
        multiPart.put("data", data);
        data.currentPassword = "invalid";
        data.displayName = "ABC";

        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", multiPart, ErrorMessage.USER_INVALID_PASSWORD);

        data.currentPassword = "password";
        data.displayName = "Darkhax";
        data.newPassword = "password1";
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", multiPart);

        data.displayName = null;
        data.newPassword = null;
        data.currentPassword = "password1";
        data.email = "testing@diluv.com";
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", multiPart);

        data.email = "lclc98@diluv.com";
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", multiPart, ErrorMessage.USER_TAKEN_EMAIL);
    }

    @Test
    public void deleteSelf () {

        Request.deleteOkWithAuth("7517d424-ccf3-4966-ac4f-d58b59508718", URL + "/self");
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

        User2FA data = new User2FA();
        data.password = "invalid";
        data.mfaStatus = "enable";
        data.mfa = gAuth.getTotpPassword(secret);
        data.mfaSecret = secret;
        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("data", data);
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self/mfa", multiPart, ErrorMessage.USER_INVALID_PASSWORD);

        data.password = "password";
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self/mfa", multiPart);
    }

    @Test
    public void getUser () {

        // Check for a non-existing user
        Request.getError(URL + "/abc", ErrorMessage.NOT_FOUND_USER);
        Request.getErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/abc", ErrorMessage.NOT_FOUND_USER);

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
        Request.getError(URL + "/abc/projects", ErrorMessage.NOT_FOUND_USER);
        Request.getErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/abc/projects", ErrorMessage.NOT_FOUND_USER);

        // Check for existing user with and without a token, and an invalid token
        Request.getOk(URL + "/darkhax/projects", "schema/project-list-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/darkhax/projects", "schema/project-list-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_JARED, URL + "/darkhax/projects", "schema/project-list-schema.json");
//        Request.getErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/darkhax/projects", 401, ErrorMessage.USER_INVALID_TOKEN);
    }

    @Test
    public void getToken () {

        Request.getError(URL + "/self/token", ErrorMessage.USER_REQUIRED_TOKEN);
        Request.getErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/self/token", ErrorMessage.USER_INVALID_TOKEN);

        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self/token", "schema/token-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_JARED, URL + "/self/token", "schema/token-schema.json");
    }

    @Test
    public void getTokens () {

        Request.getError(URL + "/self/tokens", ErrorMessage.USER_REQUIRED_TOKEN);

        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self/tokens", "schema/token-list-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_JARED, URL + "/self/tokens", "schema/token-list-schema.json");
    }

    @Test
    public void postTokens () {

        Map<String, Object> multiPart = new HashMap<>();
        Request.postErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/self/tokens", multiPart, ErrorMessage.USER_INVALID_TOKEN);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self/tokens", multiPart, ErrorMessage.TOKEN_INVALID_NAME);

        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self/tokens?name=Jenkins", multiPart, "schema/create-token-schema.json");
    }

    @Test
    public void deleteTokens () {

        Request.deleteErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/self/tokens/3", ErrorMessage.USER_INVALID_TOKEN);
        Request.deleteErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self/tokens/2", ErrorMessage.TOKEN_INVALID_ID);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + TestUtil.TOKEN_DARKHAX);
        Request.deleteRequest(URL + "/self/tokens/3", headers, 204, null);

        Request.deleteErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self/tokens/3", ErrorMessage.TOKEN_INVALID_ID);

    }
}