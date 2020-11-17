package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataProjectType;
import com.diluv.api.data.DataSlugName;
import com.google.gson.annotations.Expose;

public class DataSiteGameProjects {

    @Expose
    private final List<DataBaseProject> projects;

    @Expose
    private final List<DataSlugName> types;

    @Expose
    private final DataProjectType currentType;

    @Expose
    private final List<DataSlugName> sorts;

    public DataSiteGameProjects (List<DataBaseProject> projects, List<DataSlugName> types, DataProjectType currentType, List<DataSlugName> sorts) {

        this.projects = projects;
        this.types = types;
        this.currentType = currentType;
        this.sorts = sorts;
    }
}
