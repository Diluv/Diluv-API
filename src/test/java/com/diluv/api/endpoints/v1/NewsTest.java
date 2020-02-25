package com.diluv.api.endpoints.v1;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class NewsTest {

    private static final String URL = "/v1/news";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @AfterAll
    public static void stop () {

        TestUtil.stop();
    }

    @Test
    public void getNews () {

        given().with().get(URL).then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/news-list-schema.json"));
    }

    @Test
    public void getNewsBySlug () {

        given().with().get(URL + "/invalid").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_NEWS.getMessage()));

        given().with().get(URL + "/example?test=test").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/news-schema.json"));
    }
}