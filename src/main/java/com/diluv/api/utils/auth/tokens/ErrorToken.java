package com.diluv.api.utils.auth.tokens;

import java.util.Collections;

import javax.ws.rs.core.Response;

public class ErrorToken extends Token {
    private final Response response;

    public ErrorToken (Response response) {

        super(-1, false, Collections.emptyList());

        this.response = response;
    }

    public Response getResponse () {

        return this.response;
    }
}
