package com.diluv.api.data.site;

import com.diluv.api.data.DataProject;
import com.diluv.confluencia.database.record.ProjectFileDependenciesEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents a file uploaded to a project.
 */
public class DataSiteProjectFileDependency {

    @Expose
    private final DataProject project;

    @Expose
    private final String type;

    public DataSiteProjectFileDependency (ProjectFileDependenciesEntity rs) {

        this.project = new DataProject(rs.getDependencyProject());
        this.type = rs.getType();
    }
}