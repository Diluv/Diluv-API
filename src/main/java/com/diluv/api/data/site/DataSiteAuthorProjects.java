package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataBaseProjectType;
import com.diluv.api.data.DataProject;
import com.diluv.api.data.DataProjectType;
import com.diluv.api.data.DataSort;
import com.diluv.api.data.DataUser;
import com.google.gson.annotations.Expose;

import javax.validation.ReportAsSingleViolation;

public class DataSiteAuthorProjects {

    @Expose
    private final DataUser user;
    @Expose
    private final List<DataProject> projects;

    public DataSiteAuthorProjects (DataUser user, List<DataProject> projects) {

        this.user = user;
        this.projects = projects;
    }
}

