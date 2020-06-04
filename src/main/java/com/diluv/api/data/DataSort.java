package com.diluv.api.data;

import com.diluv.confluencia.database.sort.Sort;
import com.google.gson.annotations.Expose;

public class DataSort {

    @Expose
    private final String sort;

    @Expose
    private final String displayName;

    public DataSort (Sort sort) {

        this.sort = sort.getSort();
        this.displayName = sort.getDisplayName();
    }
}
