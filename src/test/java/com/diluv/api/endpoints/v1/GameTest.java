package com.diluv.api.endpoints.v1;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;

import java.io.File;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class GameTest {

    private static final String URL = "/v1/games";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void getGames () {

        given().with().get(URL).then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/game-list-schema.json"));
    }

    @Test
    public void getSort () {

        given().with().get(URL + "/sort").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/sort-schema.json"));
    }

    @Test
    public void getGameBySlug () {

        given().with().get(URL + "/invalid").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        given().with().get(URL + "/minecraft-je").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/game-schema.json"));
    }

    @Test
    public void getProjectTypesByGameSlug () {

        given().with().get(URL + "/invalid/types").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        given().with().get(URL + "/minecraft-je/types").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-types-list-schema.json"));
    }

    @Test
    public void getProjectTypesByGameSlugAndProjectType () {

        get(URL + "/invalid/invalid").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft-je/invalid").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft-je/mods").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-types-schema.json"));
    }

    @Test
    public void getProjectsByGameSlugAndProjectType () {

        get(URL + "/invalid/invalid/projects").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft-je/invalid/projects").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft-je/mods/projects").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
    }

    @Test
    public void getProjectFeed () {

        get(URL + "/invalid/invalid/feed.atom").then().assertThat().statusCode(400);
        get(URL + "/minecraft-je/invalid/feed.atom").then().assertThat().statusCode(400);
        get(URL + "/minecraft-je/mods/feed.atom").then().assertThat().statusCode(200);
    }

    @Test
    public void getProjectByGameSlugAndProjectTypeAndProjectSlug () {

        get(URL + "/invalid/invalid/test").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft-je/invalid/test").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft-je/mods/test").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT.getMessage()));

        get(URL + "/minecraft-je/mods/bookshelf").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-schema.json"));
    }

    @Test
    public void getProjectFilesFeed () {

        get(URL + "/invalid/invalid/test/feed.atom").then().assertThat().statusCode(400);
        get(URL + "/minecraft-je/invalid/test/feed.atom").then().assertThat().statusCode(400);
        get(URL + "/minecraft-je/mods/test/feed.atom").then().assertThat().statusCode(400);
        get(URL + "/minecraft-je/mods/bookshelf/feed.atom").then().assertThat().statusCode(200);
    }

    @Test
    public void getProjectFilesByGameSlugAndProjectTypeAndProjectSlug () {

        get(URL + "/invalid/invalid/test/files").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft-je/invalid/invalidproject/files").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));

        get(URL + "/minecraft-je/mods/invalidproject/files").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT.getMessage()));

        get(URL + "/minecraft-je/mods/crafttweaker/files").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-files-list-schema.json"));

        get(URL + "/minecraft-je/mods/bookshelf/files").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-files-list-schema.json"));
    }

    @Test
    public void postProjectByGameSlugAndProjectType () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());

        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).multiPart("name", "Bookshelf").multiPart("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("logo", logo).with().post(URL + "/invalid/invalid").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));
        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).multiPart("name", "Bookshelf").multiPart("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("logo", logo).with().post(URL + "/minecraft-je/invalid").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT_TYPE.getMessage()));
        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).multiPart("name", "Bookshelf").multiPart("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("logo", logo).with().post(URL + "/minecraft-je/mods").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.PROJECT_TAKEN_SLUG.getMessage()));
        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).multiPart("name", "Bookshelf2").multiPart("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").multiPart("logo", logo).with().post(URL + "/minecraft-je/mods").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-schema.json"));
    }
}