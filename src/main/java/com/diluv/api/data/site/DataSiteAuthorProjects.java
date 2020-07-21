package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataProject;
import com.diluv.api.data.DataSort;
import com.diluv.api.data.DataUser;
import com.google.gson.annotations.Expose;

public class DataSiteAuthorProjects {

    @Expose
    private final DataUser user;
    @Expose
    private final List<DataProject> projects;

    @Expose
    private final List<DataSort> sort;

    @Expose
    private final long projectCount;

    public DataSiteAuthorProjects (DataUser user, List<DataProject> projects, List<DataSort> sort, long projectCount) {

        this.user = user;
        this.projects = projects;
        this.sort = sort;
        this.projectCount = projectCount;
    }
}

