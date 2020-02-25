package com.diluv.api.endpoints.v1;

import com.google.gson.annotations.Expose;

/**
 * This response type is used to respond with an error message.
 */
public class ErrorResponse implements IResponse {
    
    /**
     * The type of error that occurred, as a string.
     */
    @Expose
    private final String error;
    
    /**
     * A short description of the error that occurred.
     */
    @Expose
    private final String message;
    
    public ErrorResponse(String error, String message) {
        
        this.error = error;
        this.message = message;
    }
}