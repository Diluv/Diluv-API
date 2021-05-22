package com.diluv.api.utils.auth;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.permissions.UserPermissions;

public class TokenValidator implements ConstraintValidator<RequireToken, Token> {

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
            context.buildConstraintViolationWithTemplate("USER_REQUIRED_TOKEN").addConstraintViolation();
            return false;
        }
        if (token.isApiToken() && !this.apiToken) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("USER_INVALID_API_TOKEN").addConstraintViolation();
            return false;
        }

        if (this.userPermissions.length != 0) {
            for (UserPermissions permission : this.userPermissions) {
                if (!UserPermissions.hasPermission(token, permission)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("USER_NOT_AUTHORIZED").addConstraintViolation();
                    return false;
                }
            }
        }

        return true;
    }
}
