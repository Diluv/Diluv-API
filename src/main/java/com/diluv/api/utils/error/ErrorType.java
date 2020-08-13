package com.diluv.api.utils.error;

import org.apache.http.HttpStatus;

public enum ErrorType {

    INTERNAL_SERVER_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Internal Server Error"),
    BAD_REQUEST(HttpStatus.SC_BAD_REQUEST, "Bad Request"),
    UNAUTHORIZED(HttpStatus.SC_UNAUTHORIZED, "Unauthorized"),
    NOT_FOUND(HttpStatus.SC_NOT_FOUND, "404"),
    ;
    int code;
    String error;

    ErrorType (int code, String error) {

        this.code = code;
        this.error = error;
    }

    public int getCode () {

        return this.code;
    }

    public String getError () {

        return this.error;
    }
}