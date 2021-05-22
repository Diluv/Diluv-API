package com.diluv.api.provider;

import com.diluv.api.utils.error.ErrorMessage;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse (ValidationException exception) {
        final String[] message = exception.getMessage().split(": ");
        ErrorMessage errorMessage = ErrorMessage.valueOf(message[1]);
        return errorMessage.respond();
    }
}
