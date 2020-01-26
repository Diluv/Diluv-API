package com.diluv.api.endpoints.v1.domain;


public class ErrorDomain implements Domain {
    private final String error;
    private final String message;

    public ErrorDomain (String error, String message) {

        this.error = error;
        this.message = message;
    }

    public String getError () {

        return this.error;
    }

    public String getMessage () {

        return this.message;
    }
}