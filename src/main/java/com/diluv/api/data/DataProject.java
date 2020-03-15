package com.diluv.api.data;

import java.util.List;

import com.diluv.confluencia.database.record.ProjectRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a project on the site.
 */
public class DataProject extends DataBaseProject {

    /**
     * The description of the project.
     */
    @Expose
    private final String description;

    @Expose
    private final List<DataProjectLink> links;

    public DataProject (ProjectRecord projectRecord, List<DataCategory> categories) {

        this(projectRecord, categories, null, null);
    }

    public DataProject (ProjectRecord projectRecord, List<DataCategory> categories, List<DataProjectContributor> projectAuthorRecords, List<DataProjectLink> links) {

        super(projectRecord, categories, projectAuthorRecords);
        this.description = projectRecord.getDescription();
        this.links = links;
    }
}