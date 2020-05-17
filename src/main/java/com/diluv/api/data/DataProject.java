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

    public DataProject (ProjectRecord projectRecord, List<DataTag> tags) {

        this(projectRecord, tags, null, null);
    }

    public DataProject (ProjectRecord projectRecord, List<DataTag> tags, List<DataProjectContributor> projectAuthorRecords, List<DataProjectLink> links) {

        super(projectRecord, tags, projectAuthorRecords);
        this.description = projectRecord.getDescription();
        this.links = links;
    }
}