package com.diluv.api.utils.error;

public enum ErrorResponse {

    INVALID_TERMS(ErrorType.BAD_REQUEST, "invalid.terms"),
    INVALID_EMAIL(ErrorType.BAD_REQUEST, "invalid.email"),
    INVALID_USERNAME(ErrorType.BAD_REQUEST, "invalid.username"),
    INVALID_PASSWORD(ErrorType.BAD_REQUEST, "invalid.password"),
    INVALID_PROJECT_NAME(ErrorType.BAD_REQUEST, "invalid.project_name"),
    INVALID_PROJECT_SUMMARY(ErrorType.BAD_REQUEST, "invalid.project_summary"),
    INVALID_PROJECT_DESCRIPTION(ErrorType.BAD_REQUEST, "invalid.project_description"),
    INVALID_VERIFICATION_CODE(ErrorType.BAD_REQUEST, "invalid.verification_code"),
    INVALID_GAME(ErrorType.BAD_REQUEST, "invalid.game"),
    INVALID_PROJECT_TYPE(ErrorType.BAD_REQUEST, "invalid.project_type"),
    INVALID_PROJECT(ErrorType.BAD_REQUEST, "invalid.project"),
    INVALID_MFA(ErrorType.BAD_REQUEST, "invalid.mfa"),
    REQUIRED_MFA(ErrorType.BAD_REQUEST, "required.mfa"),

    INVALID_TOKEN(ErrorType.UNAUTHORIZED, "invalid.token"),

    PASSWORD_MISMATCH(ErrorType.INTERNAL_SERVER_ERROR, "mismatch.password"),
    INVALID_PASSWORD_TYPE(ErrorType.INTERNAL_SERVER_ERROR, "invalid.token"),
    UNVERIFIED_USER(ErrorType.BAD_REQUEST, "unverified.user"),

    TAKEN_EMAIL(ErrorType.BAD_REQUEST, "taken.email"),
    TAKEN_USERNAME(ErrorType.BAD_REQUEST, "taken.username"),
    TAKEN_SLUG(ErrorType.BAD_REQUEST, "taken.slug"),

    FAILED_CREATE_USER_REFRESH(ErrorType.INTERNAL_SERVER_ERROR, "database.create_user_refresh"),
    FAILED_CREATE_TEMP_USER(ErrorType.INTERNAL_SERVER_ERROR, "database.create_temp_user"),
    FAILED_CREATE_USER(ErrorType.INTERNAL_SERVER_ERROR, "database.create_user"),
    FAILED_CREATE_PROJECT(ErrorType.INTERNAL_SERVER_ERROR, "database.create_project"),
    FAILED_DELETE_TEMP_USER(ErrorType.INTERNAL_SERVER_ERROR, "database.delete_temp_user"),

    NOT_FOUND_GAME(ErrorType.BAD_REQUEST, "notfound.game"),
    NOT_FOUND_PROJECT_TYPE(ErrorType.BAD_REQUEST, "notfound.project_type"),
    NOT_FOUND_USER(ErrorType.BAD_REQUEST, "notfound.user"),
    NOT_FOUND_PROJECT(ErrorType.BAD_REQUEST, "notfound.project"),


    INTERNAL_SERVER_ERROR(ErrorType.INTERNAL_SERVER_ERROR, "error");
    private final ErrorType type;
    private final String message;

    ErrorResponse (ErrorType type, String message) {

        this.type = type;
        this.message = message;
    }

    public ErrorType getType () {

        return type;
    }

    public String getMessage () {

        return message;
    }
}
