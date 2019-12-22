package com.diluv.api.endpoints.v1;

import java.io.File;
import java.util.Calendar;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.error.ErrorResponse;
import com.nimbusds.jose.JOSEException;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class ProjectTest {

    private static final String URL = "/v1/games";

    private static String darkhaxToken;

    @BeforeAll
    public static void setup () throws JOSEException {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        darkhaxToken = JWTUtil.generateAccessToken(0, "darkhax", calendar.getTime());

        TestUtil.start();
    }

    @AfterAll
    public static void stop () {

        TestUtil.stop();
    }

    @Test
    public void testProjectTypesByGameSlugAndProjectType () {

        get(URL + "/eco/mods").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/maps").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft/mods").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/project-types-schema.json"));
    }

    @Test
    public void testProjectsByGameSlugAndProjectType () {

        get(URL + "/eco/mods/projects").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/maps/projects").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft/mods/projects").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
    }

    @Test
    public void testProjectByGameSlugAndProjectTypeAndProject () {

        get(URL + "/eco/mods/test").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/maps/test").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft/mods/test").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_PROJECT.getMessage()));

        get(URL + "/minecraft/mods/bookshelf").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/project-schema.json"));
    }

    @Test
    public void testProjectFilesByGameSlugAndProjectTypeAndProject () {

        get(URL + "/eco/mods/test/files").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/maps/mapproject/files").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft/mods/invalidproject/files").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_PROJECT.getMessage()));

        get(URL + "/minecraft/mods/crafttweaker/files").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/project-files-list-schema.json"));

        get(URL + "/minecraft/mods/bookshelf/files").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/project-files-list-schema.json"));
    }

    @Test
    public void testCreateProject () {

        given()
            .header("Authorization", "Bearer " + darkhaxToken)
            .with().post(URL + "/eco/mods").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_GAME.getMessage()));

        given()
            .header("Authorization", "Bearer " + darkhaxToken)
            .with().post(URL + "/minecraft/maps").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_PROJECT_TYPE.getMessage()));
        ClassLoader classLoader = getClass().getClassLoader();
        given()
            .header("Authorization", "Bearer " + darkhaxToken)
            .formParam("name", "Bookshelf")
            .formParam("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
            .formParam("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
            .multiPart("logo", new File(classLoader.getResource("logo.png").getFile()))
            .with().post(URL + "/minecraft/mods").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.PROJECT_TAKEN_SLUG.getMessage()));

        given()
            .header("Authorization", "Bearer " + darkhaxToken)
            .formParam("name", "Bookshelf2")
            .formParam("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
            .formParam("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
            .multiPart("logo", new File(classLoader.getResource("logo.png").getFile()))
            .with().post(URL + "/minecraft/mods").then().assertThat().statusCode(200)
            .body(matchesJsonSchemaInClasspath("schema/project-schema.json"));
    }
}