package com.diluv.api.endpoints.v1;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.endpoints.v1.domain.ErrorDomain;
import com.diluv.api.endpoints.v1.game.domain.GameDomain;
import com.diluv.api.endpoints.v1.game.domain.ProjectTypeDomain;
import com.diluv.api.utils.FileReader;
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

        TestUtil.getTest(httpClient, BASE_URL, FileReader.readJsonFileByType("games/getAllGames", GameDomain.class));
    }

    @Test
    public void testGameBySlug () throws IOException {

        TestUtil.getTest(httpClient, BASE_URL + "/eco", FileReader.readJsonFile("games/getECO", ErrorDomain.class));
        TestUtil.getTest(httpClient, BASE_URL + "/minecraft", FileReader.readJsonFileByType("games/getMinecraft", GameDomain.class));
    }

    @Test
    public void testProjectTypesByGameSlug () throws IOException {

        TestUtil.getTest(httpClient, BASE_URL + "/eco/types", FileReader.readJsonFile("game_types/getAllECO", ErrorDomain.class));
        TestUtil.getTest(httpClient, BASE_URL + "/minecraft/types", FileReader.readJsonFileByListType("game_types/getAllMinecraft", ProjectTypeDomain.class));
    }
}