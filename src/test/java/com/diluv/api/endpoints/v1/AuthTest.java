package com.diluv.api.endpoints.v1;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.TestUtil;

public class AuthTest {

    private static final String BASE_URL = "/v1/auth";

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
    public void testRegister () throws IOException {

    }
}