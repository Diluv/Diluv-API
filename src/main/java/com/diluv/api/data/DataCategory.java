package com.diluv.api.data;

import com.diluv.confluencia.database.record.CategoryRecord;
import com.google.gson.annotations.Expose;

public class DataCategory {

    @Expose
    private final String slug;

    @Expose
    private final String name;

    @Expose
    private final String iconURL;

    public DataCategory (CategoryRecord rs) {

        this.slug = rs.getSlug();
        this.name = rs.getName();
        this.iconURL = rs.getIconURL();
    }
}
