package com.diluv.api.provider;

import com.diluv.api.DiluvAPIServer;
import com.diluv.api.utils.error.ErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse (Throwable exception) {

        DiluvAPIServer.LOGGER.catching(exception);
        return ErrorMessage.THROWABLE.respond();
    }
}
