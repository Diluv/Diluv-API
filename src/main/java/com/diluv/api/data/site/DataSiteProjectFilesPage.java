package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataBaseProject;
import com.google.gson.annotations.Expose;

public class DataSiteProjectFilesPage {

    @Expose
    private final DataBaseProject project;

    @Expose
    private final List<DataSiteProjectFileDisplay> files;

    @Expose
    private final long fileCount;

    public DataSiteProjectFilesPage (DataBaseProject project, List<DataSiteProjectFileDisplay> files, long fileCount) {

        this.project = project;
        this.files = files;
        this.fileCount = fileCount;
    }
}
