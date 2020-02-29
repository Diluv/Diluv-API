package com.diluv.api.endpoints.v1;

import java.io.File;
import java.util.Calendar;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.error.ErrorMessage;
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

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        darkhaxToken = new AccessToken(0, "darkhax").generate(calendar.getTime());

        TestUtil.start();
    }

    @AfterAll
    public static void stop () {

        TestUtil.stop();
    }

    @Test
    public void getProjectTypesByGameSlugAndProjectType () {

        get(URL + "/invalid/mods").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/maps").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft/mods").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-types-schema.json"));
    }

    @Test
    public void getProjectsByGameSlugAndProjectType () {

        get(URL + "/invalid/mods/projects").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/maps/projects").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft/mods/projects").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
    }

    @Test
    public void getProjectByGameSlugAndProjectTypeAndProjectSlug () {

        get(URL + "/invalid/mods/test").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/maps/test").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft/mods/test").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT.getMessage()));

        get(URL + "/minecraft/mods/bookshelf").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-schema.json"));
    }

    @Test
    public void getProjectFilesByGameSlugAndProjectTypeAndProjectSlug () {

        get(URL + "/invalid/mods/test/files").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/maps/mapproject/files").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft/mods/invalidproject/files").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT.getMessage()));

        get(URL + "/minecraft/mods/crafttweaker/files").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-files-list-schema.json"));

        get(URL + "/minecraft/mods/bookshelf/files").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-files-list-schema.json"));
    }

    @Test
    public void postProjectByGameSlugAndProjectType () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());

        given().header("Authorization", "Bearer " + darkhaxToken).multiPart("name", "Bookshelf").multiPart("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("logo", logo).with().post(URL + "/invalid/mods").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        given().header("Authorization", "Bearer " + darkhaxToken).multiPart("name", "Bookshelf").multiPart("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("logo", logo).with().post(URL + "/minecraft/maps").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        given().header("Authorization", "Bearer " + darkhaxToken).multiPart("name", "Bookshelf").multiPart("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("logo", logo).with().post(URL + "/minecraft/mods").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.PROJECT_TAKEN_SLUG.getMessage()));

        given().header("Authorization", "Bearer " + darkhaxToken).multiPart("name", "Bookshelf2").multiPart("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("logo", logo).with().post(URL + "/minecraft/mods").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-schema.json"));
    }

    @Test
    public void postProjectFilesByGameSlugAndProjectTypeAndProjectSlug () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());
        given().header("Authorization", "Bearer " + darkhaxToken).multiPart("releaseType", "release").multiPart("classifier", "binary").multiPart("changelog", "Changelog").multiPart("filename", "logo.png").multiPart("file", logo).with().post(URL + "/minecraft/mods/bookshelf/files").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-files-schema.json"));
    }
}