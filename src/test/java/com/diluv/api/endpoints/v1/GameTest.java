package com.diluv.api.endpoints.v1;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;

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
    public void getGames () {
        
        given().with().get(URL).then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/game-list-schema.json"));
    }
    
    @Test
    public void getGameBySlug () {
        
        given().with().get(URL + "/invalid").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));
        
        given().with().get(URL + "/minecraft").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/game-schema.json"));
    }
    
    @Test
    public void getProjectTypesByGameSlug () {
        
        given().with().get(URL + "/invalid/types").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_GAME.getMessage()));
        
        given().with().get(URL + "/minecraft/types").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-types-list-schema.json"));
    }
}