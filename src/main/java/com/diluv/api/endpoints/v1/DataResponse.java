package com.diluv.api.endpoints.v1;

import com.google.gson.annotations.Expose;

public class DataResponse<T> implements Response {
    
    @Expose
    private final T data;
    
    public DataResponse(T data) {
        
        this.data = data;
    }
}