package com.diluv.api.endpoints.v1;

import java.util.Calendar;

import com.diluv.api.utils.auth.AccessToken;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.error.ErrorResponse;
import com.nimbusds.jose.JOSEException;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class UserTest {
    private static final String URL = "/v1/users";

    private static String darkhaxToken;
    private static String jaredlll08Token;
    private static String invalidToken;

    @BeforeAll
    public static void setup () throws JOSEException {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        darkhaxToken = new AccessToken(0, "darkhax").generate(calendar.getTime());
        jaredlll08Token = new AccessToken(1, "jaredlll08").generate(calendar.getTime());

        invalidToken = "broken token";

        TestUtil.start();
    }

    @AfterAll
    public static void stop () {

        TestUtil.stop();
    }

    @Test
    public void testUserByUsername () {

        // /user/me tests
        given()
            .with().get(URL + "/me").then().assertThat().statusCode(401)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.USER_REQUIRED_TOKEN.getMessage()));

        given()
            .header("Authorization", "Bearer " + darkhaxToken)
            .with().get(URL + "/me").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/user-schema.json"));

        given()
            .header("Authorization", "Bearer " + jaredlll08Token)
            .with().get(URL + "/me").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/user-schema.json"));

        given()
            .header("Authorization", "Bearer " + invalidToken)
            .with().get(URL + "/me").then().assertThat().statusCode(401)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.USER_INVALID_TOKEN.getMessage()));

        // Check for a non-existing user
        given()
            .with().get(URL + "/abc").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_USER.getMessage()));
        given()
            .header("Authorization", "Bearer " + invalidToken)
            .with().get(URL + "/abc").then().assertThat().statusCode(401)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.USER_INVALID_TOKEN.getMessage()));
        given()
            .header("Authorization", "Bearer " + darkhaxToken)
            .with().get(URL + "/abc").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_USER.getMessage()));

        // Check for existing user with and without a token, and an invalid token
        given()
            .with().get(URL + "/darkhax").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/user-schema.json"));
        given()
            .header("Authorization", "Bearer " + darkhaxToken)
            .with().get(URL + "/darkhax").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/user-schema.json"));
        given()
            .header("Authorization", "Bearer " + jaredlll08Token)
            .with().get(URL + "/darkhax").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/user-schema.json"));
        given()
            .header("Authorization", "Bearer " + invalidToken)
            .with().get(URL + "/darkhax").then().assertThat().statusCode(401)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.USER_INVALID_TOKEN.getMessage()));

        // Check for wrong authorization
        given()
            .header("Authorization", "Bearer " + darkhaxToken)
            .with().get(URL + "/jaredlll08").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/user-schema.json"));
    }

    @Test
    public void testProjectsByUsername () {

        // /user/me/projects tests
        given()
            .with().get(URL + "/me/projects").then().assertThat().statusCode(401)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.USER_REQUIRED_TOKEN.getMessage()));

        given()
            .header("Authorization", "Bearer " + invalidToken)
            .with().get(URL + "/me/projects").then().assertThat().statusCode(401)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.USER_INVALID_TOKEN.getMessage()));

        given()
            .header("Authorization", "Bearer " + darkhaxToken)
            .with().get(URL + "/me/projects").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
        given()
            .header("Authorization", "Bearer " + jaredlll08Token)
            .with().get(URL + "/me/projects").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));

        // Check for a non-existing user
        given()
            .with().get(URL + "/abc/projects").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_USER.getMessage()));

        given()
            .header("Authorization", "Bearer " + darkhaxToken)
            .with().get(URL + "/abc/projects").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_USER.getMessage()));

        // Check for existing user with and without a token, and an invalid token
        given()
            .with().get(URL + "/darkhax/projects").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
        given()
            .header("Authorization", "Bearer " + darkhaxToken)
            .with().get(URL + "/darkhax/projects").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
        given()
            .header("Authorization", "Bearer " + jaredlll08Token)
            .with().get(URL + "/darkhax/projects").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
        given()
            .header("Authorization", "Bearer " + invalidToken)
            .with().get(URL + "/darkhax/projects").then().assertThat().statusCode(401)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.USER_INVALID_TOKEN.getMessage()));

        // Check for wrong authorization
        given()
            .header("Authorization", "Bearer " + darkhaxToken)
            .with().get(URL + "/jaredlll08/projects").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
    }
}