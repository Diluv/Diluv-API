package com.diluv.api.utils.error;

public enum ErrorMessage {
    
    // User Errors
    USER_INVALID_MFA(ErrorType.BAD_REQUEST, "user.invalid.mfa"),
    USER_INVALID_TERMS(ErrorType.BAD_REQUEST, "user.invalid.terms"),
    USER_INVALID_EMAIL(ErrorType.BAD_REQUEST, "user.invalid.email"),
    USER_INVALID_TOKEN(ErrorType.UNAUTHORIZED, "user.invalid.token"),
    USER_INVALID_REFRESH_TOKEN(ErrorType.UNAUTHORIZED, "user.invalid.refresh_token"),
    USER_INVALID_USERNAME(ErrorType.BAD_REQUEST, "user.invalid.username"),
    USER_INVALID_PASSWORD(ErrorType.BAD_REQUEST, "user.invalid.password"),
    USER_INVALID_VERIFICATION_CODE(ErrorType.BAD_REQUEST, "user.invalid.verification_code"),
    USER_INVALID_PASSWORD_TYPE(ErrorType.INTERNAL_SERVER_ERROR, "user.invalid.password_type"),
    USER_REQUIRED_TOKEN(ErrorType.UNAUTHORIZED, "user.required.token"),
    USER_REQUIRED_MFA(ErrorType.BAD_REQUEST, "user.required.mfa"),
    USER_NOT_AUTHORIZED(ErrorType.UNAUTHORIZED, "user.unauthorized"),
    
    USER_TAKEN_EMAIL(ErrorType.BAD_REQUEST, "user.taken.email"),
    USER_TAKEN_USERNAME(ErrorType.BAD_REQUEST, "user.taken.username"),
    USER_VERIFIED(ErrorType.BAD_REQUEST, "user.verified"),
    USER_NOT_VERIFIED(ErrorType.BAD_REQUEST, "user.not_verified"),
    USER_WRONG_PASSWORD(ErrorType.BAD_REQUEST, "user.wrong.password"),
    USER_BLACKLISTED_EMAIL(ErrorType.BAD_REQUEST, "user.blacklisted.email"),
    
    // Game Errors
    GAME_INVALID_SLUG(ErrorType.BAD_REQUEST, "game.invalid.slug"),
    
    // Project Errors
    PROJECT_INVALID_SLUG(ErrorType.BAD_REQUEST, "project.invalid.slug"),
    PROJECT_INVALID_NAME(ErrorType.BAD_REQUEST, "project.invalid.name"),
    PROJECT_INVALID_LOGO(ErrorType.BAD_REQUEST, "project.invalid.logo"),
    PROJECT_INVALID_SUMMARY(ErrorType.BAD_REQUEST, "project.invalid.summary"),
    PROJECT_INVALID_DESCRIPTION(ErrorType.BAD_REQUEST, "project.invalid.description"),
    PROJECT_TAKEN_SLUG(ErrorType.BAD_REQUEST, "project.taken.slug"),
    
    PROJECT_FILE_INVALID_CHANGELOG(ErrorType.BAD_REQUEST, "project_file.invalid.changelog"),
    PROJECT_FILE_INVALID_FILE(ErrorType.BAD_REQUEST, "project_file.invalid.file"),
    
    FILE_INVALID_SIZE(ErrorType.BAD_REQUEST, "file.invalid.size"),
    
    // Project Type Errors
    PROJECT_TYPE_INVALID_SLUG(ErrorType.BAD_REQUEST, "project_type.invalid.slug"),
    
    FAILED_CREATE_USER_REFRESH(ErrorType.INTERNAL_SERVER_ERROR, "database.create_user_refresh"),
    FAILED_CREATE_TEMP_USER(ErrorType.INTERNAL_SERVER_ERROR, "database.create_temp_user"),
    FAILED_CREATE_USER(ErrorType.INTERNAL_SERVER_ERROR, "database.create_user"),
    FAILED_CREATE_PROJECT(ErrorType.INTERNAL_SERVER_ERROR, "database.create_project"),
    FAILED_CREATE_PROJECT_FILE(ErrorType.INTERNAL_SERVER_ERROR, "database.create_project_file"),
    FAILED_DELETE_TEMP_USER(ErrorType.INTERNAL_SERVER_ERROR, "database.delete_temp_user"),
    FAILED_DELETE_REFRESH_TOKEN(ErrorType.INTERNAL_SERVER_ERROR, "database.delete_refresh_token"),
    FAILED_SHA512(ErrorType.INTERNAL_SERVER_ERROR, "failed.sha512"),
    
    FAILED_CREATE_EMAIL_SEND(ErrorType.INTERNAL_SERVER_ERROR, "database.create_email_sent"),
    FAILED_SEND_EMAIL(ErrorType.INTERNAL_SERVER_ERROR, "email.send"),
    
    NOT_FOUND_GAME(ErrorType.BAD_REQUEST, "notfound.game"),
    NOT_FOUND_PROJECT_TYPE(ErrorType.BAD_REQUEST, "notfound.project_type"),
    NOT_FOUND_USER(ErrorType.BAD_REQUEST, "notfound.user"),
    NOT_FOUND_PROJECT(ErrorType.BAD_REQUEST, "notfound.project"),
    NOT_FOUND_USER_REFRESH_TOKEN(ErrorType.BAD_REQUEST, "notfound.user_refresh_token"),
    NOT_FOUND_NEWS(ErrorType.BAD_REQUEST, "notfound.news"),
    
    FORM_INVALID(ErrorType.BAD_REQUEST, "form.invalid"),
    ERROR_TOKEN(ErrorType.BAD_REQUEST, "error.token"),
    ERROR_WRITING(ErrorType.INTERNAL_SERVER_ERROR, "error.writing"),
    ERROR_ALGORITHM(ErrorType.INTERNAL_SERVER_ERROR, "error.algorithm"),
    ERROR_SAVING_IMAGE(ErrorType.INTERNAL_SERVER_ERROR, "error.saving_image"),
    
    NEWS_INVALID_SLUG(ErrorType.BAD_REQUEST, "news.invalid.slug"),
    COOLDOWN_EMAIL(ErrorType.BAD_REQUEST, "cooldown.email");
    
    private final ErrorType type;
    private final String message;
    
    ErrorMessage(ErrorType type, String message) {
        
        this.type = type;
        this.message = message;
    }
    
    public ErrorType getType () {
        
        return this.type;
    }
    
    public String getMessage () {
        
        return this.message;
    }
}
