package com.diluv.api.data;

import com.diluv.confluencia.database.sort.Sort;
import com.google.gson.annotations.Expose;

public class DataSort {

    @Expose
    private final String slug;

    @Expose
    private final String displayName;

    public DataSort (Sort sort) {

        this.slug = sort.getSlug();
        this.displayName = sort.getDisplayName();
    }
}
