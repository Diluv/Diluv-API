package com.diluv.api.data.site;

import com.diluv.api.data.DataBaseProject;
import com.google.gson.annotations.Expose;

public class DataSiteProjectFilePage {

    @Expose
    private final DataBaseProject project;

    @Expose
    private final DataSiteProjectFileDisplay file;


    public DataSiteProjectFilePage (DataBaseProject project,  DataSiteProjectFileDisplay file) {

        this.project = project;
        this.file = file;
    }
}
