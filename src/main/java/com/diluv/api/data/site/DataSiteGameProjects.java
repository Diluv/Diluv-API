package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataBaseProjectType;
import com.google.gson.annotations.Expose;

public class DataSiteGameProjects {


    @Expose
    private final List<DataBaseProject> projects;

    @Expose
    private final List<DataBaseProjectType> types;

    @Expose
    private final DataSiteProjectType currentType;

    @Expose
    private final List<String> sorts;

    public DataSiteGameProjects (List<DataBaseProject> projects, List<DataBaseProjectType> types, DataSiteProjectType currentType, List<String> sorts) {

        this.projects = projects;
        this.types = types;
        this.currentType = currentType;
        this.sorts = sorts;
    }
}
