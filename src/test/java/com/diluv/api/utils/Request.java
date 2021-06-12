package com.diluv.api.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.diluv.api.utils.error.ErrorMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class Request {

    private static final Gson GSON = new GsonBuilder().create();

    public static ValidatableResponse getRequest (String url, int status, String schema, Map<String, String> headers) {

        ValidatableResponse response = given()
            .headers(headers)
            .get(url)
            .then()
            .assertThat()
            .statusCode(status);
        if (schema == null) {
            return response;
        }
        return response.body(matchesJsonSchemaInClasspath(schema));
    }

    public static ValidatableResponse getFeed (String url, int status) {

        return getRequest(url, status, null, new HashMap<>());
    }

    public static void getOk (String url, String schema) {

        getRequest(url, 200, schema, new HashMap<>());
    }

    public static void getOkWithAuth (String token, String url, String schema) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        getRequest(url, 200, schema, headers);
    }

    public static void getError (String url, ErrorMessage error) {

        getRequest(url, error.getType().getCode(), "schema/error-schema.json", new HashMap<>())
            .body("message", equalTo(error.getMessage()));
    }

    public static void getErrorWithAuth (String token, String url, ErrorMessage error) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        getRequest(url, error.getType().getCode(), "schema/error-schema.json", headers)
            .body("error", equalTo(error.getUniqueId()));
    }

    public static ValidatableResponse postRequest (String url, Map<String, String> headers, Map<String, Object> multiPart, int status, String schema) {

        RequestSpecification request = given().headers(headers);
        for (String key : multiPart.keySet()) {
            Object o = multiPart.get(key);
            if (o instanceof File) {
                request = request.multiPart(key, (File) o);
            }
            else if (o instanceof String) {
                request = request.multiPart(key, (String) o);
            }
            else {
                request = request.multiPart(key, GSON.toJson(o), "application/json");
            }
        }
        ValidatableResponse response = request
            .post(url)
            .then()
            .assertThat()
            .statusCode(status);

        if (schema == null) {
            return response;
        }
        return response.body(matchesJsonSchemaInClasspath(schema));
    }

    public static void postOkWithAuth (String token, String url, Map<String, Object> multiPart, String schema) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        postRequest(url, headers, multiPart, 200, schema);
    }

    public static void postErrorWithAuth (String token, String url, Map<String, Object> multiPart, ErrorMessage error) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        postRequest(url, headers, multiPart, error.getType().getCode(), "schema/error-schema.json")
            .body("error", equalTo(error.getUniqueId()));
    }

    public static ValidatableResponse patchRequest (String url, Map<String, String> headers, Object body, int status, String schema) {

        RequestSpecification request = given().headers(headers);
        ValidatableResponse response = request
            .body(GSON.toJson(body))
            .contentType(ContentType.JSON)
            .patch(url)
            .then()
            .assertThat()
            .statusCode(status);

        if (schema == null) {
            return response;
        }
        return response.body(matchesJsonSchemaInClasspath(schema));
    }

    public static ValidatableResponse patchMultiPartRequest (String url, Map<String, String> headers, Map<String, Object> multiPart, int status, String schema) {

        RequestSpecification request = given().headers(headers);
        for (String key : multiPart.keySet()) {
            Object o = multiPart.get(key);
            if (o instanceof File) {
                request = request.multiPart(key, (File) o);
            }
            else if (o instanceof String) {
                request = request.multiPart(key, (String) o);
            }
            else {
                request = request.multiPart(key, GSON.toJson(o), "application/json");
            }
        }
        ValidatableResponse response = request
            .patch(url)
            .then()
            .assertThat()
            .statusCode(status);

        if (schema == null) {
            return response;
        }
        return response.body(matchesJsonSchemaInClasspath(schema));
    }

    public static void patchErrorWithAuth (String token, String url, Object body, ErrorMessage error) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        patchRequest(url, headers, body, error.getType().getCode(), "schema/error-schema.json")
            .body("message", equalTo(error.getMessage()));
    }

    public static void patchMultipartErrorWithAuth (String token, String url, Map<String, Object> multiPart, int status, ErrorMessage error) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        patchMultiPartRequest(url, headers, multiPart, status, "schema/error-schema.json")
            .body("message", equalTo(error.getMessage()));
    }

    public static void patchOkMultipartWithAuth (String token, String url, Map<String, Object> multiPart) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        patchMultiPartRequest(url, headers, multiPart, 204, null);
    }

    public static void patchOkWithAuth (String token, String url, int statusCode, Object body) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        patchRequest(url, headers, body, statusCode, null);
    }

    public static ValidatableResponse deleteRequest (String url, Map<String, String> headers, int status, String schema) {

        RequestSpecification request = given().headers(headers);
        ValidatableResponse response = request
            .delete(url)
            .then()
            .assertThat()
            .statusCode(status);

        if (schema == null) {
            return response;
        }
        return response.body(matchesJsonSchemaInClasspath(schema));
    }

    public static void deleteOkWithAuth (String token, String url) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        deleteRequest(url, headers, 204, null);
    }

    public static void deleteErrorWithAuth (String token, String url, ErrorMessage error) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        deleteRequest(url, headers, error.getType().getCode(), "schema/error-schema.json")
            .body("error", equalTo(error.getUniqueId()));
    }
}
