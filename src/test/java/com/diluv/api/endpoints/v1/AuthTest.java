package com.diluv.api.endpoints.v1;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;

public class AuthTest {

    private static final String URL = "/v1/auth";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void register () {

        // Valid user
        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("email", "testing@diluv.com");
        multiPart.put("username", "testing");
        multiPart.put("password", "CDyDJZ4aHYTyDQhNcL9N");
        multiPart.put("terms", "true");
        Request.postOk(URL + "/register", multiPart, null);

        // Password compromised
        multiPart.put("email", "testing2@diluv.com");
        multiPart.put("username", "testing2");
        multiPart.put("password", "password");
        Request.postError(URL + "/register", multiPart, 400, ErrorMessage.USER_COMPROMISED_PASSWORD);

        // Banned domains
        multiPart.put("email", "testing2@banned.com");
        multiPart.put("password", "CDyDJZ4aHYTyDQhNcL9N");
        Request.postError(URL + "/register", multiPart, 400, ErrorMessage.USER_BLACKLISTED_EMAIL);

        multiPart.put("email", "testing2@banned2.com");
        Request.postError(URL + "/register", multiPart, 400, ErrorMessage.USER_BLACKLISTED_EMAIL);

        // Email used
        multiPart.put("email", "lclc98@diluv.com");
        Request.postError(URL + "/register", multiPart, 400, ErrorMessage.USER_TAKEN_EMAIL);

        // Username used
        multiPart.put("email", "testing2@diluv.com");
        multiPart.put("username", "lclc98");
        Request.postError(URL + "/register", multiPart, 400, ErrorMessage.USER_TAKEN_USERNAME);

        // Terms false
        multiPart.put("terms", "false");
        Request.postError(URL + "/register", multiPart, 400, ErrorMessage.USER_INVALID_TERMS);
    }

    @Test
    public void verify () {

        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("email", "tempuser@diluv.com");
        multiPart.put("code", "c1632ff7-367e-485f-91dd-92ab75903fa4");
        Request.postOk(URL + "/verify", multiPart, null);

        multiPart.put("email", "darkhax@diluv.com");
        multiPart.put("code", "1");
        Request.postError(URL + "/verify", multiPart, 400, ErrorMessage.USER_VERIFIED);

        multiPart.put("email", "jaredlll08@diluv.com");
        Request.postError(URL + "/verify", multiPart, 400, ErrorMessage.USER_VERIFIED);

        multiPart.put("email", "testing@diluv.com");
        Request.postError(URL + "/verify", multiPart, 400, ErrorMessage.NOT_FOUND_USER);
    }

    @Test
    public void checkUsername () {

        Request.getError(URL + "/checkusername/lclc98", 400, ErrorMessage.USER_TAKEN_USERNAME);
        Request.getError(URL + "/checkusername/darkhax", 400, ErrorMessage.USER_TAKEN_USERNAME);

        Request.getOk(URL + "/checkusername/nonexisting", null);
    }

    @Test
    public void resend () {

        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("username", "jaredlll08");
        multiPart.put("email", "jaredlll08@diluv.com");
        Request.postError(URL + "/resend", multiPart, 400, ErrorMessage.NOT_FOUND_USER);

        multiPart.put("username", "tempuser2");
        multiPart.put("email", "tempuser2@diluv.com");
        Request.postOk(URL + "/resend", multiPart, null);
    }

    @Test
    public void requestResetPassword () {

        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("username", "jaredlll08");
        multiPart.put("email", "jaredlll08@diluv.com");
        Request.postOk(URL + "/request-reset-password", multiPart, null);
    }

    @Test
    public void resetPassword () {

        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("email", "jaredlll08@diluv.com");
        multiPart.put("password", "password");
        multiPart.put("code", "invalid");
        Request.postError(URL + "/reset-password", multiPart, 400, ErrorMessage.NOT_FOUND_PASSWORD_RESET);

        multiPart.put("code", "daf1f148-effd-400e-9b65-a4bf96e5215d");
        Request.postError(URL + "/reset-password", multiPart, 400, ErrorMessage.USER_COMPROMISED_PASSWORD);

        multiPart.put("password", "qjeghExvF4SA6HuJLWjM");
        Request.postOk(URL + "/reset-password", multiPart, null);
    }
}