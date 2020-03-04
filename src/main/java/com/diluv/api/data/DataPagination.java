package com.diluv.api.data;

import java.util.List;

import com.google.gson.annotations.Expose;

public class DataPagination<T> {

    @Expose
    private final List<T> data;

    @Expose
    private final String cursor;

    public DataPagination (List<T> data, String cursor) {

        this.data = data;
        this.cursor = cursor;
    }

}
