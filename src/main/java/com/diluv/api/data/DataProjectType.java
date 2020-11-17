package com.diluv.api.data;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.confluencia.database.record.ProjectTypesEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents a supported project type for a supported game.
 */
public class DataProjectType extends DataSlugName {

    /**
     * The slug of the game the project type belongs to.
     */
    @Expose
    private final DataSlugName game;

    @Expose
    private final List<DataSlugName> tags;

    @Expose
    private final Long projectCount;

    @Expose
    private final List<DataSlugName> loaders;

    public DataProjectType (ProjectTypesEntity rs) {

        super(rs.getSlug(), rs.getName());
        this.game = new DataSlugName(rs.getGame().getSlug(), rs.getGame().getName());
        this.tags = rs.getTags().stream().map(a -> new DataSlugName(a.getSlug(), a.getName())).collect(Collectors.toList());
        this.projectCount = null;
        this.loaders = rs.getProjectTypeLoaders().stream().map(a -> new DataSlugName(a.getSlug(), a.getName())).collect(Collectors.toList());
    }

    public DataProjectType (ProjectTypesEntity rs, long projectCount) {

        super(rs.getSlug(), rs.getName());
        this.game = new DataSlugName(rs.getGame().getSlug(), rs.getGame().getName());
        this.tags = rs.getTags().stream().map(a -> new DataSlugName(a.getSlug(), a.getName())).collect(Collectors.toList());
        this.projectCount = projectCount;
        this.loaders = rs.getProjectTypeLoaders().stream().map(a -> new DataSlugName(a.getSlug(), a.getName())).collect(Collectors.toList());
    }
}