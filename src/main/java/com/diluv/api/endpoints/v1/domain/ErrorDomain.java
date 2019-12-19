package com.diluv.api.endpoints.v1.domain;

import com.diluv.api.utils.ErrorType;
import com.fasterxml.jackson.annotation.JsonCreator;

public class ErrorDomain implements Domain {
    private final String error;
    private final String message;

    @JsonCreator
    private ErrorDomain (String error, String message) {

        this.error = error;
        this.message = message;
    }

    public ErrorDomain (ErrorType type, String message) {

        this(type.getError(), message);
    }

    public String getError () {

        return this.error;
    }

    public String getMessage () {

        return this.message;
    }
}