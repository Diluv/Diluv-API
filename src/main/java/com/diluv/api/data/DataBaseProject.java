package com.diluv.api.data;

import java.util.ArrayList;
import java.util.List;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a subset of project info.
 */
public class DataBaseProject {

    @Expose
    private final long id;

    /**
     * The display name of the project.
     */
    @Expose
    private final String name;

    /**
     * A unique slug used to identify the project in URLs and API requests.
     */
    @Expose
    private final String slug;

    /**
     * A short summary of the project.
     */
    @Expose
    private final String summary;

    /**
     * The logo url of the project.
     */
    @Expose
    private final String logo;

    /**
     * The amount of downloads the project has.
     */
    @Expose
    private final long downloads;

    /**
     * The creation data of the project.
     */
    @Expose
    private final long createdAt;

    /**
     * The date when the project was last updated.
     */
    @Expose
    private final long updatedAt;

    /**
     * The users who contributed to the project.
     */
    @Expose
    private final List<DataTag> tags;

    /**
     * The users who contributed to the project.
     */
    @Expose
    private final List<DataProjectContributor> contributors = new ArrayList<>();

    public DataBaseProject (ProjectRecord projectRecord, List<DataTag> tags) {

        this(projectRecord, tags, null);
    }

    public DataBaseProject (ProjectRecord projectRecord, List<DataTag> tags, List<DataProjectContributor> projectAuthorRecords) {

        this.id = projectRecord.getId();
        this.name = projectRecord.getName();
        this.slug = projectRecord.getSlug();
        this.summary = projectRecord.getSummary();
        this.logo = Constants.getProjectLogo(projectRecord.getGameSlug(), projectRecord.getProjectTypeSlug(), projectRecord.getId());
        this.downloads = projectRecord.getCachedDownloads();
        this.createdAt = projectRecord.getCreatedAt();
        this.updatedAt = projectRecord.getUpdatedAt();
        this.tags = tags;
        this.contributors.add(new DataProjectContributor(projectRecord.getUserId(), projectRecord.getUsername(), projectRecord.getUserDisplayName(), projectRecord.getUserCreatedAt(), "owner"));
        if (projectAuthorRecords != null) {
            this.contributors.addAll(projectAuthorRecords);
        }
    }
}