package com.diluv.api.endpoints.v1;

import java.io.IOException;
import java.util.Calendar;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jwt.profile.JwtGenerator;

import com.diluv.api.utils.Constants;
import com.diluv.api.utils.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ProjectTest {

    private static final String BASE_URL = "/v1/games";

    private static CloseableHttpClient httpClient;

    @BeforeAll
    public static void setup () throws JsonProcessingException {

        httpClient = HttpClientBuilder.create().disableAutomaticRetries().build();

        TestUtil.start();
    }

    @AfterAll
    public static void stop () {

        TestUtil.stop();
    }

    @Test
    public void testProjectTypesByGameSlugAndProjectType () throws IOException {

        TestUtil.runTest(httpClient, BASE_URL + "/eco/mods", "{\"data\":{\"name\":\"Mods\",\"slug\":\"mods\",\"gameSlug\":\"eco\"}}");
        TestUtil.runTest(httpClient, BASE_URL + "/minecraft/mods", "{\"data\":{\"name\":\"Mods\",\"slug\":\"mods\",\"gameSlug\":\"minecraft\"}}");
    }

    @Test
    public void testProjectsByGameSlugAndProjectType () throws IOException {

//        TestUtil.runTest(httpClient, BASE_URL + "/eco/mods/projects", "{\"data\":{\"name\":\"Mods\",\"slug\":\"mods\",\"gameSlug\":\"eco\"}}");
        TestUtil.runTest(httpClient, BASE_URL + "/minecraft/mods/projects", "{\"data\":[{\"name\":\"Bookshelf\",\"slug\":\"bookshelf\",\"summary\":\"Bookshelf summary\",\"description\":\"Bookshelf description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":32923285,\"createdAt\":1573482394,\"updatedAt\":1573482394},{\"name\":\"Caliper\",\"slug\":\"caliper\",\"summary\":\"Caliper summary\",\"description\":\"Caliper description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":3176949,\"createdAt\":1573482589,\"updatedAt\":1573482589},{\"name\":\"CraftTweaker\",\"slug\":\"crafttweaker\",\"summary\":\"CraftTweaker summary\",\"description\":\"CraftTweaker description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":43825671,\"createdAt\":1573482589,\"updatedAt\":1573482589}]}");
    }

    @Test
    public void testProjectByGameSlugAndProjectTypeAndProject () throws IOException {

        TestUtil.runTest(httpClient, BASE_URL + "/minecraft/mods/crafttweaker/", "{\"data\":{\"name\":\"CraftTweaker\",\"slug\":\"crafttweaker\",\"summary\":\"CraftTweaker summary\",\"description\":\"CraftTweaker description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":43825671,\"createdAt\":1573482589,\"updatedAt\":1573482589}}");
        TestUtil.runTest(httpClient, BASE_URL + "/minecraft/mods/bookshelf", "{\"data\":{\"name\":\"Bookshelf\",\"slug\":\"bookshelf\",\"summary\":\"Bookshelf summary\",\"description\":\"Bookshelf description\",\"logoUrl\":\"https://via.placeholder.com/150\",\"cachedDownloads\":32923285,\"createdAt\":1573482394,\"updatedAt\":1573482394}}");
    }

    @Test
    public void testProjectFilesByGameSlugAndProjectTypeAndProject () throws IOException {

        TestUtil.runTest(httpClient, BASE_URL + "/minecraft/mods/crafttweaker/files", "{\"data\":[]}");
        TestUtil.runTest(httpClient, BASE_URL + "/minecraft/mods/bookshelf/files", "{\"data\":[{\"name\":\"Bookshelf-1.12.2-2.3.585.jar\",\"sha512\":\"50F5166D25155211D1A3D0AE5A4309B8EC5113D4AB920443F716B99E363012B24BA922840483158BE97A35FCB2A7A99389BDBD58839C89335A86215A29A3B09C\",\"crc32\":\"1c3b0d0a\",\"size\":270336,\"changelog\":\"Added a way to get the amount of experience points dropped by an entity.\",\"createdAt\":1573611851,\"updatedAt\":1573611851}]}");
    }
}