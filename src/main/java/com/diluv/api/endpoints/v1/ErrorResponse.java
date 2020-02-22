package com.diluv.api.endpoints.v1;

import com.google.gson.annotations.Expose;

public class ErrorResponse implements Response {
    
    @Expose
    private final String error;
    
    @Expose
    private final String message;
    
    public ErrorResponse(String error, String message) {
        
        this.error = error;
        this.message = message;
    }
}