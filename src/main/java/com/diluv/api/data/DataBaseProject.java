package com.diluv.api.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.FeaturedProjectsEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents a subset of project info.
 */
public class DataBaseProject extends DataSlugName {

    @Expose
    private final long id;

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
    private final List<DataSlugName> tags;

    /**
     * The game data related to the project
     */
    @Expose
    private final DataSlugName game;

    /**
     * The project type data related to the project
     */
    @Expose
    private final DataSlugName projectType;

    /**
     * The users who contributed to the project.
     */
    @Expose
    private final List<DataProjectContributor> contributors = new ArrayList<>();

    public DataBaseProject (FeaturedProjectsEntity featuredProject) {

        this(featuredProject.getProject());
    }

    public DataBaseProject (ProjectsEntity rs) {

        super(rs.getSlug(), rs.getName());
        this.id = rs.getId();
        this.summary = rs.getSummary();
        this.logo = Constants.getProjectLogo(rs);
        this.downloads = rs.getCachedDownloads();
        this.createdAt = rs.getCreatedAt().getTime();
        this.updatedAt = rs.getUpdatedAt().getTime();
        this.tags = rs.getTags().stream().map(a -> new DataSlugName(a.getTag().getSlug(), a.getTag().getName())).collect(Collectors.toList());
        this.game = new DataSlugName(rs.getGame().getSlug(), rs.getGame().getName());
        this.projectType = new DataSlugName(rs.getProjectType().getSlug(), rs.getProjectType().getName());
        this.contributors.add(new DataProjectContributor(rs.getOwner(), "owner"));
        if (!rs.getAuthors().isEmpty()) {
            this.contributors.addAll(rs.getAuthors().stream().map(DataProjectContributor::new).collect(Collectors.toList()));
        }
    }

    public long getId () {

        return this.id;
    }
}