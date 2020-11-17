package com.diluv.api.data;

import com.google.gson.annotations.Expose;

public class DataSlugName {

    @Expose
    private final String slug;

    @Expose
    private final String name;

    public DataSlugName (String slug, String name) {

        this.slug = slug;
        this.name = name;
    }
}
