package com.diluv.api.endpoints.v1;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.diluv.api.v1.games.ProjectCreate;

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

        Request.getOk(URL + "/minecraft-je/forge-mods", "schema/project-types-schema.json");
    }

    @Test
    public void getProjectsByGameSlugAndProjectType () {

        Request.getError(URL + "/invalid/invalid/projects", 400, ErrorMessage.NOT_FOUND_GAME);
        Request.getError(URL + "/minecraft-je/invalid/projects", 400, ErrorMessage.NOT_FOUND_PROJECT_TYPE);

        Request.getOk(URL + "/minecraft-je/forge-mods/projects", "schema/project-list-schema.json");
        Request.getOk(URL + "/minecraft-je/forge-mods/projects?sort=name", "schema/project-list-schema.json");
        Request.getOk(URL + "/minecraft-je/forge-mods/projects?name=book", "schema/project-list-schema.json");
        Request.getOk(URL + "/minecraft-je/forge-mods/projects?tags=magic&tags=tech", "schema/project-list-schema.json");
    }

    @Test
    public void getProjectFeed () {

        Request.getFeed(URL + "/invalid/invalid/feed.atom", 400);
        Request.getFeed(URL + "/minecraft-je/invalid/feed.atom", 400);
        Request.getFeed(URL + "/minecraft-je/forge-mods/feed.atom", 200);
    }

    @Test
    public void getProjectByGameSlugAndProjectTypeAndProjectSlug () {

        Request.getError(URL + "/invalid/invalid/test", 400, ErrorMessage.NOT_FOUND_GAME);
        Request.getError(URL + "/minecraft-je/invalid/test", 400, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
        Request.getError(URL + "/minecraft-je/forge-mods/test", 400, ErrorMessage.NOT_FOUND_PROJECT);

        Request.getOk(URL + "/minecraft-je/forge-mods/bookshelf", "schema/project-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/forge-mods/bookshelf", "schema/project-schema.json");
    }

    @Test
    public void getProjectFilesFeed () {

        Request.getFeed(URL + "/invalid/invalid/invalid/feed.atom", 400);
        Request.getFeed(URL + "/minecraft-je/invalid/invalid/feed.atom", 400);
        Request.getFeed(URL + "/minecraft-je/forge-mods/invalid/feed.atom", 400);
        Request.getFeed(URL + "/minecraft-je/forge-mods/bookshelf/feed.atom", 200);
    }

    @Test
    public void getProjectFilesByGameSlugAndProjectTypeAndProjectSlug () {

        Request.getError(URL + "/invalid/invalid/test/files", 400, ErrorMessage.NOT_FOUND_GAME);
        Request.getError(URL + "/minecraft-je/invalid/invalidproject/files", 400, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
        Request.getError(URL + "/minecraft-je/forge-mods/invalidproject/files", 400, ErrorMessage.NOT_FOUND_PROJECT);

        Request.getOk(URL + "/minecraft-je/forge-mods/crafttweaker/files", "schema/project-files-list-schema.json");
        Request.getOk(URL + "/minecraft-je/forge-mods/bookshelf/files", "schema/project-files-list-schema.json");
    }

    @Test
    public void postProject () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());

        ProjectCreate data =new ProjectCreate();
        data.name ="Bookshelf";
        data.summary ="Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        data.description ="Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("logo", logo);
        multiPart.put("data", data);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/invalid/invalid", multiPart, 400, ErrorMessage.NOT_FOUND_GAME);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/invalid", multiPart, 400, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/forge-mods", multiPart, 400, ErrorMessage.PROJECT_TAKEN_SLUG);

//        multiPart.put("name", "Bookshelf3");
//        multiPart.put("tags", "invalid,magic");
//        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/forge-mods", multiPart, 400, ErrorMessage.PROJECT_INVALID_TAGS);

        // Ok
        data.name="Bookshelf2";
        data.tags.clear();
        multiPart.put("data", data);
        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/forge-mods", multiPart, "schema/project-schema.json");

        data.name="Bookshelf3";
        data.tags.add("tech");
        data.tags.add("magic");
        multiPart.put("data", data);
        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/forge-mods", multiPart, "schema/project-schema.json");
    }

    @Test
    public void patchProject () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());

        ProjectCreate data =new ProjectCreate();
        data.name = "Bookshelf";
        data.summary = "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        data.description = "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("data", data);
        multiPart.put("logo", logo);
        Request.patchMultipartErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/invalid/invalid/invalid", multiPart, 400, ErrorMessage.NOT_FOUND_GAME);
        Request.patchMultipartErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/invalid/invalid", multiPart, 400, ErrorMessage.NOT_FOUND_PROJECT_TYPE);

//        multiPart.put("name", "Dark Elevators New");
//        multiPart.put("tags", "invalid,magic");
//        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/forge-mods/dark-elevators", multiPart, 400, ErrorMessage.PROJECT_INVALID_TAGS);

        // Ok
        data.name= "Dark Elevators New";
        data.tags.clear();
        multiPart.put("data", data);
        Request.patchOkMultipartWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/forge-mods/dark-elevators", multiPart);

//        multiPart.put("name", "Dark Elevators New");
//        multiPart.put("tags", "tech,magic");
//        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/forge-mods/dark-elevators", multiPart, "schema/project-schema.json");
    }
}