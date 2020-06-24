package com.diluv.api.data.site;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataProject;
import com.google.gson.annotations.Expose;

import java.util.List;

public class DataSiteProjectFilesPage {

    @Expose
    private final DataBaseProject project;
    @Expose
    private final List<DataSiteProjectFileDisplay> files;

    public DataSiteProjectFilesPage (DataBaseProject project, List<DataSiteProjectFileDisplay> files) {

        this.project = project;

        this.files = files;
    }
}
