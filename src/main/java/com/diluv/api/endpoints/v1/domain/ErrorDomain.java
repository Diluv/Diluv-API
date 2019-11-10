package com.diluv.api.endpoints.v1.domain;

import com.diluv.api.utils.ErrorType;

public class ErrorDomain implements Domain {
    private final ErrorType type;
    private final String message;

    public ErrorDomain (ErrorType type, String message) {

        this.type = type;
        this.message = message;
    }

    public String getError () {

        return type.getError();
    }

    public String getMessage () {

        return message;
    }
}