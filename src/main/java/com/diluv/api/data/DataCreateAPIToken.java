package com.diluv.api.data;

import java.util.UUID;

import com.google.gson.annotations.Expose;

public class DataCreateAPIToken {

    @Expose
    private final String name;

    @Expose
    private final String token;

    public DataCreateAPIToken (String name, UUID uuid) {

        this.name = name;
        this.token = uuid.toString();
    }
}
