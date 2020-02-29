package com.diluv.api.endpoints.v1;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class UserTest {
    private static final String URL = "/v1/users";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @AfterAll
    public static void stop () {

        TestUtil.stop();
    }

    @Test
    public void getUserByUsername () {

        // /user/self tests
        given().with().get(URL + "/self").then().assertThat().statusCode(401).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_REQUIRED_TOKEN.getMessage()));

        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).with().get(URL + "/self").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/user-schema.json"));

        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN_TWO).with().get(URL + "/self").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/user-schema.json"));

        given().header("Authorization", "Bearer " + TestUtil.INVALID_TOKEN).with().get(URL + "/self").then().assertThat().statusCode(401).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_INVALID_TOKEN.getMessage()));

        // Check for a non-existing user
        given().with().get(URL + "/abc").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_USER.getMessage()));
        // TODO        given().header("Authorization", "Bearer " + TestUtil.INVALID_TOKEN).with().get(URL + "/abc").then().assertThat().statusCode(401).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_INVALID_TOKEN.getMessage()));
        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).with().get(URL + "/abc").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_USER.getMessage()));

        // Check for existing user with and without a token, and an invalid token
        given().with().get(URL + "/darkhax").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/user-schema.json"));
        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).with().get(URL + "/darkhax").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/user-schema.json"));
        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN_TWO).with().get(URL + "/darkhax").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/user-schema.json"));
        // TODO       given().header("Authorization", "Bearer " + TestUtil.INVALID_TOKEN).with().get(URL + "/darkhax").then().assertThat().statusCode(401).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_INVALID_TOKEN.getMessage()));

        // Check for wrong authorization
        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).with().get(URL + "/jaredlll08").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/user-schema.json"));
    }

    @Test
    public void testProjectsByUsername () {

        // Check for a non-existing user
        given().with().get(URL + "/abc/projects").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));

        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).with().get(URL + "/abc/projects").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));

        // Check for existing user with and without a token, and an invalid token
        given().with().get(URL + "/darkhax/projects").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).with().get(URL + "/darkhax/projects").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN_TWO).with().get(URL + "/darkhax/projects").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
        // TODO given().header("Authorization", "Bearer " + TestUtil.INVALID_TOKEN).with().get(URL + "/darkhax/projects").then().assertThat().statusCode(401).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.USER_INVALID_TOKEN.getMessage()));

        // Check for wrong authorization
        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).with().get(URL + "/jaredlll08/projects").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
    }
}