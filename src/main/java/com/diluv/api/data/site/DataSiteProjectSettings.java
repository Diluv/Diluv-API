package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataSlugName;
import com.google.gson.annotations.Expose;

public class DataSiteProjectSettings {

    @Expose
    private final DataBaseProject project;

    @Expose
    private final List<DataSlugName> tags;

    public DataSiteProjectSettings (DataBaseProject project, List<DataSlugName> tags) {

        this.project = project;
        this.tags = tags;
    }
}