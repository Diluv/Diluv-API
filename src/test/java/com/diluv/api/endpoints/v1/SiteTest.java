package com.diluv.api.endpoints.v1;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;

public class SiteTest {

    private static final String URL = "/v1/site";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }
}