package com.diluv.api.utils.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.diluv.api.utils.permissions.UserPermissions;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {TokenValidator.class})
public @interface RequireToken {

    String message () default "";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};

    boolean apiToken () default true;

    UserPermissions[] userPermissions () default {};

}
