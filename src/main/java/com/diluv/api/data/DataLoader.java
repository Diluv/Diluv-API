package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectTypeLoadersEntity;
import com.google.gson.annotations.Expose;

public class DataLoader {

    @Expose
    private final String name;

    @Expose
    private final String slug;

    public DataLoader (ProjectTypeLoadersEntity rs) {

        this.name = rs.getName();
        this.slug = rs.getSlug();
    }
}
