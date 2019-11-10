package com.diluv.api.endpoints.v1.domain;

public class DataDomain<T> implements Domain {
    private T data;

    private DataDomain () {

    }

    public DataDomain (T data) {

        this.data = data;
    }

    public T getData () {

        return data;
    }
}
