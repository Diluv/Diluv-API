package com.diluv.api.data;

import java.util.List;

import com.google.gson.annotations.Expose;

public class DataProjectList {

    @Expose
    private final List<DataBaseProject> projects;

    public DataProjectList (List<DataBaseProject> projects) {

        this.projects = projects;
    }
}
