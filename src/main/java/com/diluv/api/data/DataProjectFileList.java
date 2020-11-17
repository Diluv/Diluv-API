package com.diluv.api.data;

import java.util.List;

import com.diluv.api.data.site.DataSiteProjectFilesPage;
import com.google.gson.annotations.Expose;

public class DataProjectFileList {

    @Expose
    private final List<DataSiteProjectFilesPage> projects;

    public DataProjectFileList (List<DataSiteProjectFilesPage> projects) {

        this.projects = projects;
    }
}
