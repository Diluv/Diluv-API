package com.diluv.api.data;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.FeaturedProjectsEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a subset of project info.
 */
public class DataBaseProject extends DataSlugName {

    @Expose
    public final long id;

    /**
     * A short summary of the project.
     */
    @Expose
    public final String summary;

    /**
     * The logo url of the project.
     */
    @Expose
    public final String logo;

    /**
     * The amount of downloads the project has.
     */
    @Expose
    public final long downloads;

    /**
     * The creation data of the project.
     */
    @Expose
    public final long createdAt;

    /**
     * The date when the project was last updated.
     */
    @Expose
    public final long updatedAt;

    /**
     * The tags of the project to be found under.
     */
    @Expose
    public final List<DataSlugName> tags;

    /**
     * The game data related to the project
     */
    @Expose
    public final DataSlugName game;

    /**
     * The project type data related to the project
     */
    @Expose
    public final DataSlugName projectType;

    /**
     * The users who contributed to the project.
     */
    @Expose
    public final List<DataProjectContributor> contributors = new ArrayList<>();

    public DataBaseProject (FeaturedProjectsEntity featuredProject) {

        this(featuredProject.getProject());
    }

    public DataBaseProject (ProjectsEntity rs) {

        this(rs, Collections.singletonList(new DataProjectContributor(rs.getOwner(), "owner")));
    }

    public DataBaseProject (ProjectsEntity rs, List<DataProjectContributor> contributors) {

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
        this.contributors.addAll(contributors);
    }
}