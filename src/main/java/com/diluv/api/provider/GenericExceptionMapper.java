package com.diluv.api.provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.diluv.api.DiluvAPIServer;
import com.diluv.api.utils.error.ErrorMessage;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse (Throwable exception) {

        DiluvAPIServer.LOGGER.catching(exception);
        return ErrorMessage.THROWABLE.respond();
    }
}
