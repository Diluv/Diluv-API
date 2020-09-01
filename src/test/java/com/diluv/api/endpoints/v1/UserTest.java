package com.diluv.api.endpoints.v1;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.v1.users.User2FAForm;
import com.diluv.api.v1.users.UserUpdateForm;
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

        Request.getError(URL + "/self", 401, ErrorMessage.USER_REQUIRED_TOKEN);
        Request.getErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/self", 401, ErrorMessage.USER_INVALID_TOKEN);

        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", "schema/user-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_JARED, URL + "/self", "schema/user-schema.json");
    }

    @Test
    public void patchSelf () {

        UserUpdateForm data = new UserUpdateForm();
        data.password = "invalid";
        data.displayName = "ABC";
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", data, 400, ErrorMessage.USER_INVALID_PASSWORD);

        data.password = "password";
        data.displayName = "Darkhax";
        data.newPassword = "password1";
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", 204, data);

        data.displayName = null;
        data.newPassword = null;
        data.email = "testing@diluv.com";
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", 204, data);

        data.email = "lclc98@diluv.com";
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self", data, 400, ErrorMessage.USER_TAKEN_EMAIL);
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

        User2FAForm data = new User2FAForm();
        data.password = "invalid";
        data.mfaStatus = "enable";
        data.mfa = gAuth.getTotpPassword(secret);
        data.mfaSecret = secret;

        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self/mfa", data, 400, ErrorMessage.USER_INVALID_PASSWORD);

        data.password = "password";
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/self/mfa", 200, data);
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