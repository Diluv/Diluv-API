package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectLinksEntity;
import com.google.gson.annotations.Expose;

public class DataProjectLink {

    @Expose
    private final String type;

    @Expose
    private final String url;

    public DataProjectLink (ProjectLinksEntity rs) {

        this.type = rs.getType();
        this.url = rs.getUrl();
    }
}