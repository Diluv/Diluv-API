package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectFileDependenciesEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents a file uploaded to a project.
 */
public class DataProjectFileDependency {

    @Expose
    private final DataBaseProject project;

    @Expose
    private final String type;

    public DataProjectFileDependency (ProjectFileDependenciesEntity rs) {

        this.project = new DataBaseProject(rs.getDependencyProject());
        this.type = rs.getType();
    }
}