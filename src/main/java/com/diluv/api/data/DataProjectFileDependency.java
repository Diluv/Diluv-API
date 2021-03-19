package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectFileDependenciesEntity;
import com.google.gson.annotations.Expose;

import org.apache.commons.lang3.StringUtils;

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
        // TODO this should ideally return a SlugName
        this.type = StringUtils.capitalize(rs.getType());
    }
}