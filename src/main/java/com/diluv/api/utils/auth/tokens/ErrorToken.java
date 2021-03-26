package com.diluv.api.utils.auth.tokens;

import com.diluv.api.utils.error.ErrorMessage;

import javax.ws.rs.core.Response;

import java.util.Collections;

public class ErrorToken extends Token {
    private final ErrorMessage errorMessage;
    private final String message;

    public ErrorToken (ErrorMessage errorMessage) {

        this(errorMessage, null);
    }

    public ErrorToken (ErrorMessage errorMessage, String message) {

        super(-1, false, Collections.emptyList());

        this.errorMessage = errorMessage;
        this.message = message;
    }

    public ErrorMessage getErrorMessage () {

        return this.errorMessage;
    }

    public String getMessage () {

        return this.message;
    }

    public Response getResponse () {

        if (this.message == null) {

            return this.errorMessage.respond();
        }

        return this.errorMessage.respond(this.message);
    }
}
