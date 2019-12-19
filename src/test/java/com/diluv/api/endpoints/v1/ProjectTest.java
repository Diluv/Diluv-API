package com.diluv.api.endpoints.v1;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.endpoints.v1.domain.ErrorDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectFileDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectTypeDomain;
import com.diluv.api.endpoints.v1.user.domain.ProjectDomain;
import com.diluv.api.utils.FileReader;
import com.diluv.api.utils.TestUtil;

public class ProjectTest {

    private static final String URL = "/v1/games";

    private static CloseableHttpClient client;

    @BeforeAll
    public static void setup () {

        client = HttpClientBuilder.create().disableAutomaticRetries().build();

        TestUtil.start();
    }

    @AfterAll
    public static void stop () {

        TestUtil.stop();
    }

    @Test
    public void testProjectTypesByGameSlugAndProjectType () throws IOException {

        TestUtil.getTest(client, URL + "/eco/mods", FileReader.readJsonFile("game_types/getEcoMods", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods", FileReader.readJsonFileByType("game_types/getMinecraftMods", ProjectTypeDomain.class));
    }

    @Test
    public void testProjectsByGameSlugAndProjectType () throws IOException {

        TestUtil.getTest(client, URL + "/eco/mods/projects", FileReader.readJsonFile("errors/game_missing", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods/projects", FileReader.readJsonFileByListType("projects/getMinecraftMods", ProjectDomain.class));
    }

    @Test
    public void testProjectByGameSlugAndProjectTypeAndProject () throws IOException {

        TestUtil.getTest(client, URL + "/eco/mods/test", FileReader.readJsonFile("errors/game_missing", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/maps/test", FileReader.readJsonFile("errors/projecttype_missing", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods/test", FileReader.readJsonFile("errors/project_missing", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods/bookshelf", FileReader.readJsonFileByType("projects/getBookshelf", ProjectDomain.class));
    }

    @Test
    public void testProjectFilesByGameSlugAndProjectTypeAndProject () throws IOException {

        TestUtil.getTest(client, URL + "/eco/mods/test/files", FileReader.readJsonFile("errors/game_missing", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods/crafttweaker/files", FileReader.readJsonFileByListType("empty_data", ProjectFileDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods/bookshelf/files", FileReader.readJsonFileByListType("project_files/getBookshelfFiles", ProjectFileDomain.class));
    }
}