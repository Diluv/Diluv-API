package com.diluv.api.utils.response;

import com.google.gson.annotations.Expose;

/**
 * This response type is used to respond with a data object.
 *
 * @param <T> The type of data to respond with.
 */
public class DataResponse<T> {

    @Expose
    private final T data;

    public DataResponse (T data) {

        this.data = data;
    }
}