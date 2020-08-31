package com.diluv.api.v1.games;

import com.google.gson.annotations.Expose;

public class FileDependency {

    @Expose
    public Long projectId;

    @Expose
    public String type;

    public FileDependency (Long projectId, String type) {

        this.projectId = projectId;
        this.type = type;
    }
}
