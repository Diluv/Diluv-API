package com.diluv.api.endpoints.v1;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class AuthTest {

    private static final String URL = "/v1/auth";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void testRegister () {

        // Valid user
        given().multiPart("email", "testing@diluv.com").multiPart("username", "testing").multiPart("password", "CDyDJZ4aHYTyDQhNcL9N").multiPart("terms", "true").with().post(URL + "/register").then().assertThat().statusCode(200);

        // Password compromised
        given().multiPart("email", "testing2@diluv.com").multiPart("username", "testing2").multiPart("password", "password").multiPart("terms", "true").with().post(URL + "/register").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_COMPROMISED_PASSWORD.getMessage()));

        // Banned domains
        given().multiPart("email", "testing2@banned.com").multiPart("username", "testing2").multiPart("password", "CDyDJZ4aHYTyDQhNcL9N").multiPart("terms", "true").with().post(URL + "/register").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_BLACKLISTED_EMAIL.getMessage()));

        given().multiPart("email", "test@banned2.com").multiPart("username", "testing2").multiPart("password", "CDyDJZ4aHYTyDQhNcL9N").multiPart("terms", "true").with().post(URL + "/register").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_BLACKLISTED_EMAIL.getMessage()));

        // Email used
        given().multiPart("email", "lclc98@diluv.com").multiPart("username", "testing2").multiPart("password", "CDyDJZ4aHYTyDQhNcL9N").multiPart("terms", "true").with().post(URL + "/register").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_TAKEN_EMAIL.getMessage()));

        // Username used
        given().multiPart("email", "testing2@diluv.com").multiPart("username", "lclc98").multiPart("password", "CDyDJZ4aHYTyDQhNcL9N").multiPart("terms", "true").with().post(URL + "/register").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_TAKEN_USERNAME.getMessage()));

        // Terms false
        given().multiPart("email", "testing2@diluv.com").multiPart("username", "lclc98").multiPart("password", "CDyDJZ4aHYTyDQhNcL9N").multiPart("terms", "false").with().post(URL + "/register").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_INVALID_TERMS.getMessage()));
    }

    @Test
    public void testLogin () {

        given().multiPart("username", "jaredlll08").multiPart("password", "password").with().post(URL + "/login").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/login-schema.json"));

        given().multiPart("username", "jaredlll08").multiPart("password", "wrongpassword").with().post(URL + "/login").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_WRONG_PASSWORD.getMessage()));

        given().multiPart("username", "darkhax").multiPart("password", "password").with().post(URL + "/login").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_REQUIRED_MFA.getMessage()));

        given().multiPart("username", "lclc98").multiPart("password", "password").with().post(URL + "/login").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_NOT_VERIFIED.getMessage()));

        given().multiPart("username", "testing").multiPart("password", "password").with().post(URL + "/login").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_USER.getMessage()));
    }

    @Test
    public void testVerify () {

        given().multiPart("email", "lclc98@diluv.com").multiPart("code", "8f32d879-45b3-4b8b-ae44-999e59566125").with().post(URL + "/verify").then().assertThat().statusCode(200);

        given().multiPart("email", "darkhax@diluv.com").multiPart("code", "1").with().post(URL + "/verify").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_VERIFIED.getMessage()));

        given().multiPart("email", "jaredlll08@diluv.com").multiPart("code", "1").with().post(URL + "/verify").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_VERIFIED.getMessage()));

        given().multiPart("email", "testing@diluv.com").multiPart("code", "1").with().post(URL + "/verify").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_USER.getMessage()));
    }

    @Test
    public void testRefresh () {

        given().with().post(URL + "/refresh").then().assertThat().statusCode(401).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_REQUIRED_TOKEN.getMessage()));

        given().header("Authorization", "Bearer " + TestUtil.VALID_REFRESH_TOKEN).with().post(URL + "/refresh").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/login-schema.json"));
    }

    @Test
    public void testCheckUsername () {

        given().with().get(URL + "/checkusername/lclc98").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_TAKEN_USERNAME.getMessage()));

        given().with().get(URL + "/checkusername/darkhax").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_TAKEN_USERNAME.getMessage()));

        given().with().get(URL + "/checkusername/nonexisting").then().assertThat().statusCode(200);
    }

    @Test
    public void testResend () {

        given().multiPart("username", "jaredlll08").multiPart("email", "jaredlll08@diluv.com").with().post(URL + "/resend").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_USER.getMessage()));

        given().multiPart("username", "test2").multiPart("email", "test2@diluv.com").with().post(URL + "/resend").then().assertThat().statusCode(200);
    }

    @Test
    public void requestResetPassword () {

        given().multiPart("username", "jaredlll08").multiPart("email", "jaredlll08@diluv.com").with().post(URL + "/request-reset-password").then().assertThat().statusCode(200);
    }

    @Test
    public void resetPassword () {

        given().multiPart("code", "invalid").multiPart("email", "jaredlll08@diluv.com").multiPart("password", "password").with().post(URL + "/reset-password").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PASSWORD_RESET.getMessage()));
        given().multiPart("code", "daf1f148-effd-400e-9b65-a4bf96e5215d").multiPart("email", "jaredlll08@diluv.com").multiPart("password", "password").with().post(URL + "/reset-password").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_COMPROMISED_PASSWORD.getMessage()));
        given().multiPart("code", "daf1f148-effd-400e-9b65-a4bf96e5215d").multiPart("email", "jaredlll08@diluv.com").multiPart("password", "qjeghExvF4SA6HuJLWjM").with().post(URL + "/reset-password").then().assertThat().statusCode(200);

        given().multiPart("username", "jaredlll08").multiPart("password", "qjeghExvF4SA6HuJLWjM").with().post(URL + "/login").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/login-schema.json"));
    }
}