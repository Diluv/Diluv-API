package com.diluv.api.utils.error;

import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.auth.InvalidTokenException;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;


// TODO Temp actually handle properly
public class ErrorHandler implements HttpHandler {

    private final HttpHandler next;

    public ErrorHandler (final HttpHandler next) {

        this.next = next;
    }

    @Override
    public void handleRequest (final HttpServerExchange ex) throws Exception {

        try {
            next.handleRequest(ex);
        }
        catch (Throwable throwable) {
            if (InvalidTokenException.class.isInstance(throwable)) {
                ResponseUtil.errorResponse(ex, ErrorResponse.USER_INVALID_TOKEN);
            }
            else {
                throw throwable;
            }
        }

    }
}