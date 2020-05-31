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
     * The tags of the project to be found under.
     */
    @Expose
    private final List<DataTag> tags;

    /**
     * The game data related to the project
     */
    @Expose
    private final DataBaseGame game;

    /**
     * The project type data related to the project
     */
    @Expose
    private final DataBaseProjectType projectType;

    /**
     * The users who contributed to the project.
     */
    @Expose
    private final List<DataProjectContributor> contributors = new ArrayList<>();

    public DataBaseProject (ProjectRecord projectRecord, List<DataTag> tags) {

        this(projectRecord, tags, null);
    }

    public DataBaseProject (ProjectRecord rs, List<DataTag> tags, List<DataProjectContributor> projectAuthorRecords) {

        this.id = rs.getId();
        this.name = rs.getName();
        this.slug = rs.getSlug();
        this.summary = rs.getSummary();
        this.logo = Constants.getProjectLogo(rs.getGameSlug(), rs.getProjectTypeSlug(), rs.getId());
        this.downloads = rs.getCachedDownloads();
        this.createdAt = rs.getCreatedAt();
        this.updatedAt = rs.getUpdatedAt();
        this.tags = tags;
        this.game = new DataBaseGame(rs.getGameSlug(), rs.getGameName());
        this.projectType = new DataBaseProjectType(rs.getProjectTypeSlug(), rs.getProjectTypeName());
        this.contributors.add(new DataProjectContributor(rs.getUserId(), rs.getUsername(), rs.getUserDisplayName(), rs.getUserCreatedAt(), "owner"));
        if (projectAuthorRecords != null) {
            this.contributors.addAll(projectAuthorRecords);
        }
    }
}