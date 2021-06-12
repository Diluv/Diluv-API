package com.diluv.api.endpoints.v1;

import org.junit.jupiter.api.BeforeAll;

import com.diluv.api.utils.TestUtil;

public class SiteTest {

    private static final String URL = "/v1/site";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }
}