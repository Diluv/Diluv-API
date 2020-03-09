package com.diluv.api.data;

import java.util.ArrayList;
import java.util.List;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a project on the site.
 */
public class DataProject {

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
     * The description of the project.
     */
    @Expose
    private final String description;

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
    private final List<DataCategory> categories;

    /**
     * The users who contributed to the project.
     */
    @Expose
    private final List<DataProjectContributor> contributors = new ArrayList<>();

    public DataProject (ProjectRecord projectRecord, List<DataCategory> categories) {

        this(projectRecord, categories, null);
    }

    public DataProject (ProjectRecord projectRecord, List<DataCategory> categories, List<DataProjectContributor> projectAuthorRecords) {

        this.name = projectRecord.getName();
        this.slug = projectRecord.getSlug();
        this.summary = projectRecord.getSummary();
        this.description = projectRecord.getDescription();
        this.logo = Constants.getLogo(projectRecord.getGameSlug(), projectRecord.getProjectTypeSlug(), projectRecord.getId());
        this.downloads = projectRecord.getCachedDownloads();
        this.createdAt = projectRecord.getCreatedAt();
        this.updatedAt = projectRecord.getUpdatedAt();
        this.categories = categories;
        this.contributors.add(new DataProjectContributor(projectRecord.getUserId(), projectRecord.getUsername(), projectRecord.getUserCreatedAt(), "owner"));
        if (projectAuthorRecords != null) {
            this.contributors.addAll(projectAuthorRecords);
        }
    }
}