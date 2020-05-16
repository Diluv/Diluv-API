package com.diluv.api.endpoints.v1;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;

public class NewsTest {

    private static final String URL = "/v1/news";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void getNews () {

        Request.getOk(URL, "schema/news-list-schema.json");
    }

    @Test
    public void getNewsBySlug () {

        Request.getError(URL + "/invalid", 400, ErrorMessage.NOT_FOUND_NEWS);

        Request.getOk(URL + "/example", "schema/news-schema.json");
    }
}