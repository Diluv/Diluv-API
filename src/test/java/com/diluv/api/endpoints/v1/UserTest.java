package com.diluv.api.endpoints.v1;

import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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