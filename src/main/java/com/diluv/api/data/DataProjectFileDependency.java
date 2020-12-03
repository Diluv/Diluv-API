package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectFileDependenciesEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents a file uploaded to a project.
 */
public class DataProjectFileDependency {

    @Expose
    private final DataProject project;

    @Expose
    private final String type;

    public DataProjectFileDependency (ProjectFileDependenciesEntity rs) {

        this.project = new DataProject(rs.getDependencyProject());
        this.type = rs.getType();
    }
}