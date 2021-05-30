package com.diluv.api.provider;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.diluv.api.utils.error.ErrorMessage;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse (ValidationException exception) {

        try {
            final String[] message = exception.getMessage().split(": ");
            try {
                ErrorMessage errorMessage = ErrorMessage.valueOf(message[1]);
                return errorMessage.respond();
            }
            catch (IllegalArgumentException e) {
                return ErrorMessage.THROWABLE.respond();
            }
        }
        catch (Exception e) {
            return ErrorMessage.THROWABLE.respond();
        }
    }
}
