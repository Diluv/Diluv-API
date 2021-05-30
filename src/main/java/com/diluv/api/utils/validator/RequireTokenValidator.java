package com.diluv.api.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.UserPermissions;

public class RequireTokenValidator implements ConstraintValidator<RequireToken, Token> {

    private boolean apiToken;
    private UserPermissions[] userPermissions;

    @Override
    public void initialize (RequireToken requireToken) {

        this.apiToken = requireToken.apiToken();
        this.userPermissions = requireToken.userPermissions();
    }

    @Override
    public boolean isValid (Token token, ConstraintValidatorContext context) {

        if (token == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorMessage.USER_REQUIRED_TOKEN.name()).addConstraintViolation();
            return false;
        }
        if (token.isApiToken() && !this.apiToken) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorMessage.USER_INVALID_API_TOKEN.name()).addConstraintViolation();
            return false;
        }

        if (this.userPermissions.length != 0) {
            for (UserPermissions permission : this.userPermissions) {
                if (!UserPermissions.hasPermission(token, permission)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(ErrorMessage.USER_NOT_AUTHORIZED.name()).addConstraintViolation();
                    return false;
                }
            }
        }

        return true;
    }
}
