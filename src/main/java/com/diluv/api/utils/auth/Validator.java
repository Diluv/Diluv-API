package com.diluv.api.utils.auth;

import com.diluv.api.utils.ResponseUtil;
import com.diluv.api.utils.error.ErrorResponse;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.EmailValidator;

public class Validator {

    public static boolean validateEmail (String email) {

        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean validateUsername (String username) {

        if (GenericValidator.isBlankOrNull(username) || username.length() > 50 || username.length() < 3) {
            return false;
        }
        return GenericValidator.matchRegexp(username, "([A-Za-z0-9-_]+)");
    }

    public static boolean validatePassword (String password) {
        return !GenericValidator.isBlankOrNull(password) && password.length() <= 70 && password.length() >= 8;
    }

    public static boolean validateProjectName (String projectName) {

        if (GenericValidator.isBlankOrNull(projectName) || projectName.length() > 50 || projectName.length() < 3) {
            return false;
        }
        return GenericValidator.matchRegexp(projectName, "([A-Za-z0-9-_:]+)");
    }

    public static boolean validateProjectSummary (String projectSummary) {

        return !GenericValidator.isBlankOrNull(projectSummary) && projectSummary.length() <= 250 && projectSummary.length() >= 10;
    }

    public static boolean validateProjectDescription (String projectDescription) {

        return !GenericValidator.isBlankOrNull(projectDescription) && projectDescription.length() <= 1000 && projectDescription.length() >= 50;
    }
}