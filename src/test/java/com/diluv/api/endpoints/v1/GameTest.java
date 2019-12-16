package com.diluv.api.endpoints.v1;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.TestUtil;

public class GameTest {

    private static final String BASE_URL = "/v1/games";

    private static CloseableHttpClient httpClient;

    @BeforeAll
    public static void setup () {

        httpClient = HttpClientBuilder.create().disableAutomaticRetries().build();

        TestUtil.start();
    }

    @AfterAll
    public static void stop () {

        TestUtil.stop();
    }

    @Test
    public void testGame () throws IOException {

        TestUtil.runTest(httpClient, BASE_URL, "{\"data\":[{\"slug\":\"eco\",\"name\":\"ECO\",\"url\":\"https://www.strangeloopgames.com/eco/\"},{\"slug\":\"minecraft\",\"name\":\"Minecraft\",\"url\":\"https://www.minecraft.net\"}]}");
    }

    @Test
    public void testGameBySlug () throws IOException {

        TestUtil.runTest(httpClient, BASE_URL + "/eco", "{\"data\":{\"slug\":\"eco\",\"name\":\"ECO\",\"url\":\"https://www.strangeloopgames.com/eco/\"}}");
        TestUtil.runTest(httpClient, BASE_URL + "/minecraft", "{\"data\":{\"slug\":\"minecraft\",\"name\":\"Minecraft\",\"url\":\"https://www.minecraft.net\"}}");
    }

    @Test
    public void testProjectTypesByGameSlug () throws IOException {

        TestUtil.runTest(httpClient, BASE_URL + "/eco/types", "{\"data\":[{\"name\":\"Mods\",\"slug\":\"mods\",\"gameSlug\":\"eco\"}]}");
        TestUtil.runTest(httpClient, BASE_URL + "/minecraft/types", "{\"data\":[{\"name\":\"Mods\",\"slug\":\"mods\",\"gameSlug\":\"minecraft\"},{\"name\":\"Resource Packs\",\"slug\":\"resourcepacks\",\"gameSlug\":\"minecraft\"}]}");
    }
}