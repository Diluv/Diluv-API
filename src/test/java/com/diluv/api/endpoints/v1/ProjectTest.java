package com.diluv.api.endpoints.v1;

import com.diluv.api.utils.Constants;
import com.diluv.api.utils.TestUtil;

import com.diluv.api.utils.error.ErrorMessage;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class ProjectTest {

    private static final String URL = "/v1/projects";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void getProjectById () {

        get(URL + "/100000000").then().assertThat().statusCode(400).body(matchesJsonSchemaInClasspath("schema/error-schema.json")).body("message", equalTo(ErrorMessage.NOT_FOUND_PROJECT.getMessage()));
        get(URL + "/1").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-schema.json"));
    }

    @Test
    public void postProjectFilesByGameSlugAndProjectTypeAndProjectSlug () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());
        given().header("Authorization", "Bearer " + TestUtil.VALID_TOKEN).multiPart("project_id", 1).multiPart("version", "1.1.0").multiPart("releaseType", "release").multiPart("classifier", "binary").multiPart("changelog", "Changelog").multiPart("filename", "logo.png").multiPart("file", logo).with().post(URL + "/files").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-files-schema.json"));
//        given().header("Authorization", "Bearer " + TestUtil.VALID_LONG_LASTING_TOKEN).multiPart("project_id", 1).multiPart("version", "1.1.1").multiPart("releaseType", "release").multiPart("classifier", "binary").multiPart("changelog", "Changelog").multiPart("filename", "logo.png").multiPart("file", logo).with().post(URL + "/files").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-files-schema.json"));
//        given().header("Authorization", "Bearer " + TestUtil.VALID_LONG_LASTING_TOKEN).multiPart("project_id", 1).multiPart("version", "1.1.2").multiPart("releaseType", "release").multiPart("classifier", "binary").multiPart("changelog", "Changelog").multiPart("filename", "logo.png").multiPart("file", logo).multiPart("game_versions", "1.12.2,1.12.1").with().post(URL + "/files").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("schema/project-files-schema.json"));
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