package com.diluv.api.endpoints.v1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.endpoints.v1.domain.ErrorDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectFileDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectTypeDomain;
import com.diluv.api.endpoints.v1.user.domain.ProjectDomain;
import com.diluv.api.utils.FileReader;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.auth.JWTUtil;
import com.nimbusds.jose.JOSEException;

public class ProjectTest {

    private static final String URL = "/v1/games";

    private static CloseableHttpClient client;
    private static String darkhaxToken;

    @BeforeAll
    public static void setup () throws JOSEException {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        darkhaxToken = JWTUtil.generateAccessToken(0, "darkhax", calendar.getTime());

        client = HttpClientBuilder.create().disableAutomaticRetries().build();

        TestUtil.start();
    }

    @AfterAll
    public static void stop () {

        TestUtil.stop();
    }

    @Test
    public void testProjectTypesByGameSlugAndProjectType () throws IOException {

        TestUtil.getTest(client, URL + "/eco/mods", FileReader.readJsonFile("errors/notfound.game", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/maps", FileReader.readJsonFile("errors/notfound.project_type", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods", FileReader.readJsonFileByType("game_types/getMinecraftMods", ProjectTypeDomain.class));
    }

    @Test
    public void testProjectsByGameSlugAndProjectType () throws IOException {

        TestUtil.getTest(client, URL + "/eco/mods/projects", FileReader.readJsonFile("errors/notfound.game", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/maps/projects", FileReader.readJsonFile("errors/notfound.project_type", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods/projects", FileReader.readJsonFileByListType("projects/getMinecraftMods", ProjectDomain.class));
    }

    @Test
    public void testProjectByGameSlugAndProjectTypeAndProject () throws IOException {

        TestUtil.getTest(client, URL + "/eco/mods/test", FileReader.readJsonFile("errors/notfound.game", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/maps/test", FileReader.readJsonFile("errors/notfound.project_type", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods/test", FileReader.readJsonFile("errors/notfound.project", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods/bookshelf", FileReader.readJsonFileByType("projects/getBookshelf", ProjectDomain.class));
    }

    @Test
    public void testProjectFilesByGameSlugAndProjectTypeAndProject () throws IOException {

        TestUtil.getTest(client, URL + "/eco/mods/test/files", FileReader.readJsonFile("errors/notfound.game", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/maps/mapproject/files", FileReader.readJsonFile("errors/notfound.project_type", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods/invalidproject/files", FileReader.readJsonFile("errors/notfound.project", ErrorDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods/crafttweaker/files", FileReader.readJsonFileByListType("empty_data", ProjectFileDomain.class));
        TestUtil.getTest(client, URL + "/minecraft/mods/bookshelf/files", FileReader.readJsonFileByListType("project_files/getBookshelfFiles", ProjectFileDomain.class));
    }

    @Test
    public void testCreateProject () throws IOException {

        List<NameValuePair> pairs = new ArrayList<>();
        TestUtil.postTestToken(client, URL + "/eco/mods", pairs, darkhaxToken, FileReader.readJsonFile("errors/notfound.game", ErrorDomain.class));
        pairs.clear();

        pairs.add(new BasicNameValuePair("name", "Bookshelf"));
        pairs.add(new BasicNameValuePair("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        pairs.add(new BasicNameValuePair("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        TestUtil.postTestToken(client, URL + "/minecraft/mods", pairs, darkhaxToken, FileReader.readJsonFile("errors/taken.slug", ErrorDomain.class));
        pairs.clear();

//        pairs.add(new BasicNameValuePair("name", "Bookshelf2"));
//        pairs.add(new BasicNameValuePair("summary", "Bookshelf summary aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
//        pairs.add(new BasicNameValuePair("description", "Bookshelf descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
//        TestUtil.postTestToken(client, URL + "/minecraft/mods", pairs, darkhaxToken, FileReader.readJsonFile("errors/taken.slug", ErrorDomain.class));
//        pairs.clear();
    }
}