package com.diluv.api.endpoints.v1;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.diluv.api.v1.games.FileDependency;
import com.diluv.api.v1.games.ProjectFileUpload;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.DiluvAPIServer;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;

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
    public void getProjectByHash () {

        Request.getOk(URL + "/hash/fake", "schema/project-list-schema.json");
        Request.getOk(URL + "/hash/5E96A9A98839D073C298BBD0AC73A510E1F13A64151E2C4895440ECDBCD6D483EDA994D2CD5E69C5C00A96783280F7BC1E933667B4A25C53CE3918007D5C77E3", "schema/project-list-schema.json");
    }

    @Test
    public void postProjectFilesByGameSlugAndProjectTypeAndProjectSlug () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());

        Map<String, Object> multiPart = new HashMap<>();
        ProjectFileUpload data = new ProjectFileUpload();
        data.version = "1.1.0";
        data.releaseType = "release";
        data.classifier = "binary";
        data.changelog = "logo.png";

        multiPart.put("data", data);
        multiPart.put("filename", "logo.png");
        multiPart.put("file", logo);
        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/1/files", multiPart, "schema/project-files-schema.json");

        // Game Version
        data.version = "1.1.1";
        data.gameVersions.add("1.15");
        data.gameVersions.add("1.15.2");
        multiPart.put("data", data);
        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/1/files", multiPart, "schema/project-files-schema.json");

        data.version = "1.1.2";
        data.gameVersions.add("invalid");
        multiPart.put("data", data);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/1/files", multiPart, 400, ErrorMessage.PROJECT_FILE_INVALID_GAME_VERSION);

        // Dependencies
        data.version = "1.1.3";
        data.gameVersions.clear();
        data.dependencies.add(new FileDependency(2L, "optional"));
        data.dependencies.add(new FileDependency(3L, "optional"));
        multiPart.put("data", data);
        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/1/files", multiPart, "schema/project-files-schema.json");

        data.version = "1.1.4";
        data.dependencies.add(new FileDependency(1L, "optional"));
        multiPart.put("data", data);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/1/files", multiPart, 400, ErrorMessage.PROJECT_FILE_INVALID_DEPEND_SELF);

        data.version = "1.1.4";
        data.dependencies.clear();
        data.dependencies.add(new FileDependency(500L, "optional"));
        multiPart.put("data", data);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/1/files", multiPart, 400, ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_ID);

        // Game Version + Dependencies
        data.version = "1.1.7";
        data.dependencies.clear();
        data.dependencies.add(new FileDependency(2L, "optional"));
        data.dependencies.add(new FileDependency(3L, "optional"));
        data.gameVersions.add("1.15");
        data.gameVersions.add("1.15.2");
        multiPart.put("data", data);
        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/1/files", multiPart, "schema/project-files-schema.json");

        data.dependencies.add(new FileDependency(3L, "invalid"));
        data.version = "1.1.8";
        multiPart.put("data", data);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/1/files", multiPart, 400, ErrorMessage.PROJECT_FILE_INVALID_DEPENDENCY_TYPE);
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