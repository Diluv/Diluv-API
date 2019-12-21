package com.diluv.api.endpoints.v1;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorResponse;

import static io.restassured.RestAssured.get;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class GameTest {

    private static final String URL = "/v1/games";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @AfterAll
    public static void stop () {

        TestUtil.stop();
    }

    @Test
    public void testGame () {

        get(URL).then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/game-list-schema.json"));
    }

    @Test
    public void testGameBySlug () {

        get(URL + "/eco").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_GAME.getMessage()));
        get(URL + "/minecraft").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/game-schema.json"));
    }

    @Test
    public void testProjectTypesByGameSlug () {

        get(URL + "/eco/types").then().assertThat().statusCode(400)
            .body(matchesJsonSchemaInClasspath("schema/error-schema.json"))
            .body("message", equalTo(ErrorResponse.NOT_FOUND_GAME.getMessage()));

        get(URL + "/minecraft/types").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-types-list-schema.json"));
    }
}