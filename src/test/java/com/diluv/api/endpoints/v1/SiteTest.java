package com.diluv.api.endpoints.v1;

import java.util.HashMap;
import java.util.Map;

import com.diluv.api.utils.error.ErrorMessage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;

import static org.hamcrest.Matchers.equalTo;

public class SiteTest {

    private static final String URL = "/v1/site";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void postProjectFileDownloads () {

        Map<String, String> headers = new HashMap<>();
        headers.put("CF-Connecting-IP", "127.0.0.1");
        Request.postRequest(URL + "/files/1/download", headers, new HashMap<>(), 204, null);
        Request.postRequest(URL + "/files/1/download", headers, new HashMap<>(), 204, null);

        headers.put("CF-Connecting-IP", "1.1.1.1");
        Request.postRequest(URL + "/files/2/download", headers,new HashMap<>(), 204, null);
        Request.postRequest(URL + "/files/3/download", headers,new HashMap<>(), 204, null);
    }
}