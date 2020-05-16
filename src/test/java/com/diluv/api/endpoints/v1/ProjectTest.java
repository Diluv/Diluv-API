package com.diluv.api.endpoints.v1;

import com.diluv.api.DiluvAPIServer;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProjectTest {

    private static final String URL = "/v1/projects";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void getProjectById () {

        Request.getError(URL + "/100000000", 400, ErrorMessage.NOT_FOUND_PROJECT);

        Request.getOk(URL + "/1", "schema/project-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/1", "schema/project-schema.json");
    }

    @Test
    public void postProjectFilesByGameSlugAndProjectTypeAndProjectSlug () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());

        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("project_id", 1);
        multiPart.put("version", "1.1.0");
        multiPart.put("releaseType", "release");
        multiPart.put("classifier", "binary");
        multiPart.put("changelog", "Changelog");
        multiPart.put("filename", "logo.png");
        multiPart.put("file", logo);
        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/files", multiPart, "schema/project-files-schema.json");

        // Game Version
        multiPart.put("version", "1.1.1");
        multiPart.put("game_versions", "1.15,1.15.2");
        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/files", multiPart, "schema/project-files-schema.json");

        multiPart.put("version", "1.1.2");
        multiPart.put("game_versions", "1.15,1.15.2,invalid");
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/files", multiPart, 400, ErrorMessage.PROJECT_FILE_INVALID_GAME_VERSION);

        // Dependencies
        multiPart.remove("game_versions");
        multiPart.put("version", "1.1.3");
        multiPart.put("dependencies", "2,3");
        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/files", multiPart, "schema/project-files-schema.json");

        multiPart.put("version", "1.1.4");
        multiPart.put("dependencies", "1,2,3");
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/files", multiPart, 400, ErrorMessage.PROJECT_FILE_INVALID_SAME_ID);

        multiPart.put("version", "1.1.5");
        multiPart.put("dependencies", "invalid");
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/files", multiPart, 400, ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_ID);

        multiPart.put("version", "1.1.6");
        multiPart.put("dependencies", "1000000");
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/files", multiPart, 400, ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_ID);

        // Game Version + Dependencies
        multiPart.put("version", "1.1.7");
        multiPart.put("dependencies", "2,3");
        multiPart.put("game_versions", "1.15,1.15.2");

        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/files", multiPart, "schema/project-files-schema.json");
    }

    @AfterAll
    public static void cleanup () {

        try {
            FileUtils.deleteDirectory(new File(Constants.PROCESSING_FOLDER));
        }
        catch (IOException e) {
            DiluvAPIServer.LOGGER.catching(e);
        }
    }
}