package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectLinkRecord;
import com.google.gson.annotations.Expose;

public class DataProjectLink {

    @Expose
    private final String type;

    @Expose
    private final String url;

    public DataProjectLink (ProjectLinkRecord rs) {

        this.type = rs.getType();
        this.url = rs.getUrl();
    }
}