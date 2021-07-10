package com.diluv.api.data;

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
    public final String createdAt;

    /**
     * The date when the project was last updated.
     */
    @Expose
    public final String updatedAt;

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
     * The users who owns the project.
     */
    @Expose
    private final DataUser owner;

    /**
     * The users who are authors to the project.
     */
    @Expose
    public final List<DataProjectAuthor> authors;

    public DataBaseProject (FeaturedProjectsEntity featuredProject) {

        this(featuredProject.getProject());
    }

    public DataBaseProject (ProjectsEntity rs) {

        this(rs, new DataUser(rs.getOwner()), rs.getAuthors().stream().map(DataProjectAuthor::new).collect(Collectors.toList()));
    }

    public DataBaseProject (ProjectsEntity rs, DataUser owner, List<DataProjectAuthor> authors) {

        super(rs.getSlug(), rs.getName());
        this.id = rs.getId();
        this.summary = rs.getSummary();
        this.logo = Constants.getProjectLogo(rs);
        this.downloads = rs.getCachedDownloads();
        this.createdAt = rs.getCreatedAt().toString();
        this.updatedAt = rs.getUpdatedAt().toString();
        this.tags = rs.getTags().stream().map(a -> new DataSlugName(a.getTag().getSlug(), a.getTag().getName())).collect(Collectors.toList());
        this.game = new DataSlugName(rs.getGame().getSlug(), rs.getGame().getName());
        this.projectType = new DataSlugName(rs.getProjectType().getSlug(), rs.getProjectType().getName());
        this.owner = owner;
        this.authors = authors;
    }
}