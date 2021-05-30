package com.diluv.api.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.validator.routines.EmailValidator;

import com.diluv.api.utils.error.ErrorMessage;

public class CustomEmailValidator implements ConstraintValidator<CustomEmail, String> {

    @Override
    public boolean isValid (String email, ConstraintValidatorContext context) {

        if (email == null) {
            return true;
        }

        if (!EmailValidator.getInstance().isValid(email)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorMessage.USER_INVALID_EMAIL.name()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
