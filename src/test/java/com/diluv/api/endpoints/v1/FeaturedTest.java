package com.diluv.api.endpoints.v1;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.TestUtil;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class FeaturedTest {

    private static final String URL = "/v1/featured";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void getGames () {

        given().with().get(URL + "/games").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/game-list-schema.json"));
    }

    @Test
    public void getProjects () {

        given().with().get(URL + "/projects").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-list-schema.json"));
    }
}