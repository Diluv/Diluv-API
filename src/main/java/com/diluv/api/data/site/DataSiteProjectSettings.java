package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataProject;
import com.diluv.api.data.DataSlugName;
import com.google.gson.annotations.Expose;

public class DataSiteProjectSettings {

    @Expose
    private final DataProject project;

    @Expose
    private final List<DataSlugName> tags;

    public DataSiteProjectSettings (DataProject project, List<DataSlugName> tags) {

        this.project = project;
        this.tags = tags;
    }
}