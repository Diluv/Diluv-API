package com.diluv.api.utils;

import com.diluv.api.utils.error.ErrorMessage;

public class MismatchException extends Exception {

    private final ErrorMessage errorMessage;

    public MismatchException (ErrorMessage errorMessage) {

        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage () {

        return this.errorMessage;
    }
}
