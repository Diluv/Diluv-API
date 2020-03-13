package com.diluv.api.endpoints.v1;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.Constants;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class ProjectTest {

    private static final String URL = "/v1/games";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void getProjectTypesByGameSlugAndProjectType () {

        get(URL + "/invalid/invalid").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/invalid").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft/mods").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-types-schema.json"));
    }

    @Test
    public void getProjectsByGameSlugAndProjectType () {

        get(URL + "/invalid/invalid/projects").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/invalid/projects").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft/mods/projects").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
    }

    @Test
    public void getProjectByGameSlugAndProjectTypeAndProjectSlug () {

        get(URL + "/invalid/invalid/test").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/invalid/test").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft/mods/test").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT.getMessage()));

        get(URL + "/minecraft/mods/bookshelf").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-schema.json"));
    }

    @Test
    public void getProjectFilesByGameSlugAndProjectTypeAndProjectSlug () {

        get(URL + "/invalid/invalid/test/files").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/invalid/invalidproject/files").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft/mods/invalidproject/files").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT.getMessage()));

        get(URL + "/minecraft/mods/crafttweaker/files").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-files-list-schema.json"));

        get(URL + "/minecraft/mods/bookshelf/files").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-files-list-schema.json"));
    }

    @Test
    public void postProjectByGameSlugAndProjectType () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());

        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).multiPart("name", "Bookshelf").multiPart("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("logo", logo).with().post(URL + "/invalid/invalid").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).multiPart("name", "Bookshelf").multiPart("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("logo", logo).with().post(URL + "/minecraft/invalid").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).multiPart("name", "Bookshelf").multiPart("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("logo", logo).with().post(URL + "/minecraft/mods").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.PROJECT_TAKEN_SLUG.getMessage()));

        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).multiPart("name", "Bookshelf2").multiPart("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("logo", logo).with().post(URL + "/minecraft/mods").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-schema.json"));
    }

    @Test
    public void postProjectFilesByGameSlugAndProjectTypeAndProjectSlug () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());
        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).multiPart("releaseType", "release").multiPart("classifier", "binary").multiPart("changelog", "Changelog").multiPart("filename", "logo.png").multiPart("file", logo).with().post(URL + "/minecraft/mods/bookshelf/files").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-files-schema.json"));
        given().header("Authorization", "Bearer " + TestUtil.VALID_LONG_LASTING_TOKEN).multiPart("releaseType", "release").multiPart("classifier", "binary").multiPart("changelog", "Changelog").multiPart("filename", "logo.png").multiPart("file", logo).with().post(URL + "/minecraft/mods/bookshelf/files").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-files-schema.json"));
    }

    @AfterAll
    public static void cleanup () {

        try {
            FileUtils.deleteDirectory(new File(Constants.PROCESSING_FOLDER));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}