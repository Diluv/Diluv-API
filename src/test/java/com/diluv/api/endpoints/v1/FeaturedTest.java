package com.diluv.api.endpoints.v1;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;

public class FeaturedTest {

    private static final String URL = "/v1/featured";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void getFeatured () {

        Request.getOk(URL, "schema/featured-schema.json");
    }
}