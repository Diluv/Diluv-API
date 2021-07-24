package com.diluv.api.provider;

import com.diluv.api.utils.error.ErrorMessage;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ResteasyViolationException> {

    @Override
    public Response toResponse (ResteasyViolationException exception) {

        try {
            if (!exception.getParameterViolations().isEmpty()) {
                final ResteasyConstraintViolation message = exception.getParameterViolations().get(0);
                ErrorMessage errorMessage = ErrorMessage.valueOf(message.getMessage());
                return errorMessage.respond();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ErrorMessage.THROWABLE.respond();
    }
}
