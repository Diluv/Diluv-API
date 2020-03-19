package com.diluv.api.provider;

import com.diluv.api.utils.error.ErrorMessage;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse (NotFoundException exception) {

        return ErrorMessage.NOT_FOUND.respond();
    }
}
