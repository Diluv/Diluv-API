package com.diluv.api.data.site;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataProject;
import com.diluv.api.data.DataTag;
import com.google.gson.annotations.Expose;

import java.util.List;

public class DataSiteProjectSettings {

    @Expose
    private final DataProject project;

    @Expose
    private final List<DataTag> tags;

    public DataSiteProjectSettings (DataProject project, List<DataTag> tags) {

        this.project = project;

        this.tags = tags;
    }
}