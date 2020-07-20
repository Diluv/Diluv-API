package com.diluv.api.provider;

import javax.ws.rs.core.Response;

public class ResponseException extends Exception {

    private final Response response;

    public ResponseException (Response response) {

        this.response = response;
    }

    public Response getResponse () {

        return this.response;
    }
}
