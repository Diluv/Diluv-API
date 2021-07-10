package com.diluv.api.endpoints.v1;

import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.api.v1.games.ProjectAuthors;
import com.diluv.api.v1.games.ProjectCreate;
import com.diluv.api.v1.games.ProjectPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

        Request.getError(URL + "/invalid", ErrorMessage.NOT_FOUND_GAME);

        Request.getOk(URL + "/minecraft-je", "schema/game-schema.json");
    }

    @Test
    public void getProjectTypesByGameSlugAndProjectType () {

        Request.getError(URL + "/invalid/invalid", ErrorMessage.NOT_FOUND_GAME);
        Request.getError(URL + "/minecraft-je/invalid", ErrorMessage.NOT_FOUND_PROJECT_TYPE);

        Request.getOk(URL + "/minecraft-je/mods", "schema/project-types-schema.json");
    }

    @Test
    public void getProjectsByGameSlugAndProjectType () {

        Request.getError(URL + "/invalid/invalid/projects", ErrorMessage.NOT_FOUND_GAME);
        Request.getError(URL + "/minecraft-je/invalid/projects", ErrorMessage.NOT_FOUND_PROJECT_TYPE);

        Request.getOk(URL + "/minecraft-je/mods/projects", "schema/project-list-schema.json");
        Request.getOk(URL + "/minecraft-je/mods/projects?sort=name", "schema/project-list-schema.json");
        Request.getOk(URL + "/minecraft-je/mods/projects?name=book", "schema/project-list-schema.json");
        Request.getOk(URL + "/minecraft-je/mods/projects?tags=magic&tags=tech", "schema/project-list-schema.json");
    }

    @Test
    public void getProjectFeed () {

        Request.getFeed(URL + "/invalid/invalid/feed.atom", 404);
        Request.getFeed(URL + "/minecraft-je/invalid/feed.atom", 404);
        Request.getFeed(URL + "/minecraft-je/mods/feed.atom", 200);
    }

    @Test
    public void getProjectByGameSlugAndProjectTypeAndProjectSlug () {

        Request.getError(URL + "/invalid/invalid/test", ErrorMessage.NOT_FOUND_GAME);
        Request.getError(URL + "/minecraft-je/invalid/test", ErrorMessage.NOT_FOUND_PROJECT_TYPE);
        Request.getError(URL + "/minecraft-je/mods/test", ErrorMessage.NOT_FOUND_PROJECT);

        Request.getOk(URL + "/minecraft-je/mods/bookshelf", "schema/project-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/bookshelf", "schema/project-schema.json");
    }

    @Test
    public void deleteProject () {

        Request.deleteErrorWithAuth(TestUtil.TOKEN_JARED, URL + "/minecraft-je/mods/bookshelf", ErrorMessage.USER_NOT_AUTHORIZED);
        Request.deleteOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/musica");
        Request.getError(URL + "/minecraft-je/mods/musica", ErrorMessage.NOT_FOUND_PROJECT);
    }

    @Test
    public void getProjectFilesFeed () {

        Request.getFeed(URL + "/invalid/invalid/invalid/feed.atom", 404);
        Request.getFeed(URL + "/minecraft-je/invalid/invalid/feed.atom", 404);
        Request.getFeed(URL + "/minecraft-je/mods/invalid/feed.atom", 404);
        Request.getFeed(URL + "/minecraft-je/mods/bookshelf/feed.atom", 200);
    }

    @Test
    public void getUploadTypeData () {

        Request.getError(URL + "/invalid/invalid/upload", ErrorMessage.NOT_FOUND_GAME);
        Request.getError(URL + "/minecraft-je/invalid/upload", ErrorMessage.NOT_FOUND_PROJECT_TYPE);

        Request.getOk(URL + "/minecraft-je/mods/upload", "schema/upload-type-schema.json");
        Request.getOk(URL + "/minecraft-je/mods/upload", "schema/upload-type-schema.json");
    }

    @Test
    public void getProjectFilesByGameSlugAndProjectTypeAndProjectSlug () {

        Request.getError(URL + "/invalid/invalid/test/files", ErrorMessage.NOT_FOUND_GAME);
        Request.getError(URL + "/minecraft-je/invalid/invalidproject/files", ErrorMessage.NOT_FOUND_PROJECT_TYPE);
        Request.getError(URL + "/minecraft-je/mods/invalidproject/files", ErrorMessage.NOT_FOUND_PROJECT);

        Request.getOk(URL + "/minecraft-je/mods/crafttweaker/files", "schema/project-files-list-schema.json");
        Request.getOk(URL + "/minecraft-je/mods/bookshelf/files", "schema/project-files-list-schema.json");
    }

    @Test
    public void postProject () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());

        ProjectCreate data = new ProjectCreate();
        data.name = "Bookshelf";
        data.summary = "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        data.description = "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("logo", logo);
        multiPart.put("data", data);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/invalid/invalid", multiPart, ErrorMessage.NOT_FOUND_GAME);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/invalid", multiPart, ErrorMessage.NOT_FOUND_PROJECT_TYPE);
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods", multiPart, ErrorMessage.PROJECT_TAKEN_SLUG);

        data.name = "Bookshelf3";
        data.tags.add("invalid");
        data.tags.add("magic");
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods", multiPart, ErrorMessage.PROJECT_INVALID_TAGS);

        // Ok
        data.name = "Bookshelf2";
        data.tags.clear();
        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods", multiPart, "schema/project-schema.json");

        data.name = "Bookshelf3";
        data.tags.add("tech");
        data.tags.add("magic");
        Request.postOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods", multiPart, "schema/project-schema.json");
        Request.postErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/minecraft-je/mods", multiPart, ErrorMessage.USER_INVALID_TOKEN);


        data.name = "ABC";
        Request.postErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods", multiPart, ErrorMessage.PROJECT_INVALID_NAME);
        Request.postErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/minecraft-je/mods", multiPart, ErrorMessage.USER_INVALID_TOKEN);
    }

    @Test
    public void patchProject () {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File logo = new File(classLoader.getResource("logo.png").getFile());

        ProjectPatch data = new ProjectPatch();
        data.name = "Bookshelf";
        data.summary = "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        data.description = "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        Map<String, Object> multiPart = new HashMap<>();
        multiPart.put("data", data);
        multiPart.put("logo", logo);
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/invalid/invalid/invalid", multiPart, 400, ErrorMessage.NOT_FOUND_GAME);
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/invalid/invalid", multiPart, 400, ErrorMessage.NOT_FOUND_PROJECT_TYPE);

//        multiPart.put("name", "Dark Elevators New");
//        multiPart.put("tags", "invalid,magic");
//        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/dark-elevators", multiPart, 400, ErrorMessage.PROJECT_INVALID_TAGS);

        // Ok
        data.name = "Dark Elevators New";
        data.tags.clear();
        multiPart.put("data", data);
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/dark-elevators", multiPart);

        multiPart.clear();
        multiPart.put("data", new ProjectPatch());
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/get-ya-tanks-here", multiPart);

        data = new ProjectPatch();
        data.ownerId = 2L;
        multiPart.clear();
        multiPart.put("data", data);
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/get-ya-tanks-here", multiPart);

        data = new ProjectPatch();
        data.ownerId = 9999L;
        multiPart.clear();
        multiPart.put("data", data);
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/get-ya-tanks-here", multiPart, ErrorMessage.NOT_FOUND_USER);

        data = new ProjectPatch();
        data.ownerId = 2L;
        multiPart.clear();
        multiPart.put("data", data);
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/cursed", multiPart, ErrorMessage.PROJECT_NOT_MEMBER);

        data = new ProjectPatch();
        data.resolveReview = true;
        multiPart.clear();
        multiPart.put("data", data);
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/waila-inhibitors", multiPart);

        data = new ProjectPatch();
        data.ownerId = 9999L;
        multiPart.clear();
        multiPart.put("data", data);
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/waila-inhibitors", multiPart, ErrorMessage.NOT_FOUND_USER);

        data = new ProjectPatch();
        ProjectAuthors authors = new ProjectAuthors();
        authors.userId = 2L;
        authors.role = "INVALID";
        data.authors.add(authors);
        multiPart.put("data", data);
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/waila-inhibitors", multiPart, ErrorMessage.INVALID_ROLE);

        data = new ProjectPatch();
        authors = new ProjectAuthors();
        authors.userId = 2L;
        authors.role = "Project lead";
        data.authors.add(authors);
        multiPart.put("data", data);
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/waila-inhibitors", multiPart);

        data = new ProjectPatch();
        authors = new ProjectAuthors();
        authors.userId = 2L;
        authors.role = "Developer";
        data.authors.add(authors);
        multiPart.put("data", data);
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/waila-inhibitors", multiPart, ErrorMessage.PROJECT_PENDING_INVITE);

        data = new ProjectPatch();
        authors = new ProjectAuthors();
        authors.userId = 2L;
        authors.role = "Developer";
        data.authors.add(authors);
        multiPart.put("data", data);
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/bookshelf", multiPart);

        authors.permissions.add("INVALID");
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/bookshelf", multiPart, ErrorMessage.INVALID_PERMISSION);

        authors.permissions.clear();
        authors.permissions.add(ProjectPermissions.PROJECT_EDIT.getName());
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/bookshelf", multiPart);

        data = new ProjectPatch();
        authors = new ProjectAuthors();
        authors.userId = 3L;
        authors.role = "Developer";
        authors.permissions.add(ProjectPermissions.PROJECT_EDIT.getName());
        data.authors.add(authors);
        multiPart.put("data", data);
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/minecraft-je/mods/waila-inhibitors", multiPart);
    }
}