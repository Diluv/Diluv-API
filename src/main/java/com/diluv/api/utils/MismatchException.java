package com.diluv.api.utils;

import com.diluv.api.utils.error.ErrorMessage;

public class MismatchException extends Exception {

    private final ErrorMessage errorMessage;
    private final String message;

    public MismatchException (ErrorMessage errorMessage, String message) {

        this.errorMessage = errorMessage;
        this.message = message;
    }

    public ErrorMessage getErrorMessage () {

        return this.errorMessage;
    }

    public String getMessage () {

        return this.message;
    }
}
