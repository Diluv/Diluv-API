package com.diluv.api.provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ResponseExceptionMapper implements ExceptionMapper<ResponseException> {

    @Override
    public Response toResponse (ResponseException exception) {

        return exception.getResponse();
    }
}
