package com.diluv.api.endpoints.v1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diluv.api.endpoints.v1.domain.ErrorDomain;
import com.diluv.api.utils.FileReader;
import com.diluv.api.utils.TestUtil;

public class AuthTest {

    private static final String URL = "/v1/auth";

    private static CloseableHttpClient client;

    @BeforeAll
    public static void setup () {

        client = HttpClientBuilder.create().disableAutomaticRetries().build();

        TestUtil.start();
    }

    @AfterAll
    public static void stop () {

        TestUtil.stop();
    }

    @Test
    public void testRegister () throws IOException {

        // Valid user
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("email", "testing@example.com"));
        pairs.add(new BasicNameValuePair("username", "testing"));
        pairs.add(new BasicNameValuePair("password", "password"));
        pairs.add(new BasicNameValuePair("terms", "true"));
        TestUtil.postTest(client, URL + "/register", pairs, null);
        pairs.clear();

        // Email used
        pairs.add(new BasicNameValuePair("email", "lclc98@example.com"));
        pairs.add(new BasicNameValuePair("username", "testing"));
        pairs.add(new BasicNameValuePair("password", "password"));
        pairs.add(new BasicNameValuePair("terms", "true"));
        TestUtil.postTest(client, URL + "/register", pairs, FileReader.readJsonFile("errors/taken.email", ErrorDomain.class));
        pairs.clear();

        // Username used
        pairs.add(new BasicNameValuePair("email", "testing@example.com"));
        pairs.add(new BasicNameValuePair("username", "lclc98"));
        pairs.add(new BasicNameValuePair("password", "password"));
        pairs.add(new BasicNameValuePair("terms", "true"));
        TestUtil.postTest(client, URL + "/register", pairs, FileReader.readJsonFile("errors/taken.username", ErrorDomain.class));
        pairs.clear();

        // Terms false
        pairs.add(new BasicNameValuePair("email", "lclc98@example.com"));
        pairs.add(new BasicNameValuePair("username", "testing"));
        pairs.add(new BasicNameValuePair("password", "password"));
        pairs.add(new BasicNameValuePair("terms", "false"));
        TestUtil.postTest(client, URL + "/register", pairs, FileReader.readJsonFile("errors/invalid.terms", ErrorDomain.class));
        pairs.clear();
    }

    @Test
    public void testLogin () throws IOException {

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("username", "darkhax"));
        pairs.add(new BasicNameValuePair("password", "password"));
        TestUtil.postTest(client, URL + "/login", pairs, FileReader.readJsonFile("errors/required.mfa", ErrorDomain.class));
        pairs.clear();

        pairs.add(new BasicNameValuePair("username", "jaredlll08"));
        pairs.add(new BasicNameValuePair("password", "password"));
//        TestUtil.postTest(httpClient, BASE_URL + "/login", pairs, //TODO Fix matching);
        pairs.clear();

        pairs.add(new BasicNameValuePair("username", "lclc98"));
        pairs.add(new BasicNameValuePair("password", "password"));
        TestUtil.postTest(client, URL + "/login", pairs, FileReader.readJsonFile("errors/unverified.user", ErrorDomain.class));
        pairs.clear();

        pairs.add(new BasicNameValuePair("username", "testing"));
        pairs.add(new BasicNameValuePair("password", "password"));
        TestUtil.postTest(client, URL + "/login", pairs, FileReader.readJsonFile("errors/notfound.user", ErrorDomain.class));
        pairs.clear();
    }

    @Test
    public void testVerify () throws IOException {

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("email", "darkhax@example.com"));
        pairs.add(new BasicNameValuePair("username", "darkhax"));
        pairs.add(new BasicNameValuePair("code", "1"));
        TestUtil.postTest(client, URL + "/verify", pairs, FileReader.readJsonFile("errors/notfound.user", ErrorDomain.class));
        pairs.clear();

        pairs.add(new BasicNameValuePair("email", "jaredlll08@example.com"));
        pairs.add(new BasicNameValuePair("username", "jaredlll08"));
        pairs.add(new BasicNameValuePair("code", "1"));
//        TestUtil.postTest(httpClient, BASE_URL + "/login", pairs, //TODO Fix matching);
        pairs.clear();

        pairs.add(new BasicNameValuePair("email", "lclc98@example.com"));
        pairs.add(new BasicNameValuePair("username", "lclc98"));
        pairs.add(new BasicNameValuePair("code", "8f32d879-45b3-4b8b-ae44-999e59566125"));
        TestUtil.postTest(client, URL + "/verify", pairs, FileReader.readJsonFile("errors/notfound.user", ErrorDomain.class));
        pairs.clear();

        pairs.add(new BasicNameValuePair("email", "testing@example.com"));
        pairs.add(new BasicNameValuePair("username", "testing"));
        pairs.add(new BasicNameValuePair("code", "1"));
        TestUtil.postTest(client, URL + "/verify", pairs, FileReader.readJsonFile("errors/notfound.user", ErrorDomain.class));
        pairs.clear();
    }
}