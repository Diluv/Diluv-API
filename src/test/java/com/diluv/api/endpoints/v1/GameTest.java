package com.diluv.api.endpoints.v1;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;

public class GameTest {

    private static final String URL = "/v1/games";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void getGames () {

        Request.getOk(URL, "schema/game-list-schema.json");
    }

    @Test
    public void getGameBySlug () {

        Request.getError(URL + "/invalid", 400, ErrorMessage.NOT_FOUND_GAME);

        Request.getOk(URL + "/minecraft-je", "schema/game-schema.json");
    }

    @Test
    public void getProjectTypesByGameSlugAndProjectType () {

        Request.getError(URL + "/invalid/invalid", 400, ErrorMessage.NOT_FOUND_GAME);
        Request.getError(URL + "/minecraft-je/invalid", 400, ErrorMessage.NOT_FOUND_PROJECT_TYPE);

        Request.getOk(URL + "/minecraft-je/mods", "schema/project-types-schema.json");
    }

    @Test
    public void getProjectsByGameSlugAndProjectType () {

        Request.getError(URL + "/invalid/invalid/projects", 400, ErrorMessage.NOT_FOUND_GAME);
        Request.getError(URL + "/minecraft-je/invalid/projects", 400, ErrorMessage.NOT_FOUND_PROJECT_TYPE);

        Request.getOk(URL + "/minecraft-je/mods/projects", "schema/project-list-schema.json");
        Request.getOk(URL + "/minecraft-je/mods/projects?sort=name", "schema/project-list-schema.json");
        Request.getOk(URL + "/minecraft-je/mods/projects?name=book", "schema/project-list-schema.json");
        Request.getOk(URL + "/minecraft-je/mods/projects?tags=magic&tags=tech", "schema/project-list-schema.json");
    }

    @Test
    public void getProjectFeed () {

        Request.getFeed(URL + "/invalid/invalid/feed.atom", 400);
        Request.getFeed(URL + "/minecraft-je/invalid/feed.atom", 400);
        Request.getFeed(URL + "/minecraft-je/mods/feed.atom", 200);
    }

    @Test
    public void getProjectByGameSlugAndProjectTypeAndProjectSlug () {

        Request.getError(URL + "/invalid/invalid/test", 400, ErrorMessage.NOT_FOUND_GAME);
        Request.getError(URL + "/minecraft-je/invalid/test", 400, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
        Request.getError(URL + "/minecraft-je/mods/test", 400, ErrorMessage.NOT_FOUND_PROJECT);

        Request.getOk(URL + "/minecraft-je/mods/bookshelf", "schema/project-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/bookshelf", "schema/project-schema.json");
    }

    @Test
    public void getProjectFilesFeed () {

        Request.getFeed(URL + "/invalid/invalid/invalid/feed.atom", 400);
        Request.getFeed(URL + "/minecraft-je/invalid/invalid/feed.atom", 400);
        Request.getFeed(URL + "/minecraft-je/mods/invalid/feed.atom", 400);
        Request.getFeed(URL + "/minecraft-je/mods/bookshelf/feed.atom", 200);
    }

    @Test
    public void getProjectFilesByGameSlugAndProjectTypeAndProjectSlug () {

        Request.getError(URL + "/invalid/invalid/test/files", 400, ErrorMessage.NOT_FOUND_GAME);
        Request.getError(URL + "/minecraft-je/invalid/invalidproject/files", 400, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
        Request.getError(URL + "/minecraft-je/mods/invalidproject/files", 400, ErrorMessage.NOT_FOUND_PROJECT);

        Request.getOk(URL + "/minecraft-je/mods/crafttweaker/files", "schema/project-files-list-schema.json");
        Request.getOk(URL + "/minecraft-je/mods/bookshelf/files", "schema/project-files-list-schema.json");
    }

    @Test
    public void postProjectByGameSlugAndProjectType () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());

        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("name", "Bookshelf");
        multiPart.put("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        multiPart.put("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        multiPart.put("logo", logo);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/invalid/invalid", multiPart, 400, ErrorMessage.NOT_FOUND_GAME);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/invalid", multiPart, 400, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods", multiPart, 400, ErrorMessage.PROJECT_TAKEN_SLUG);

        // Ok
        multiPart.put("name", "Bookshelf2");
        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods", multiPart, "schema/project-schema.json");
    }
}