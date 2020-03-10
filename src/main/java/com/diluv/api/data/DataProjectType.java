package com.diluv.api.data;

import java.util.List;

import com.diluv.confluencia.database.record.ProjectTypeRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents a supported project type for a supported game.
 */
public class DataProjectType {

    /**
     * The display name for the project type.
     */
    @Expose
    private final String name;

    /**
     * The slug for the project type.
     */
    @Expose
    private final String slug;

    /**
     * The slug of the game the project type belongs to.
     */
    @Expose
    private final String gameSlug;

    /**
     * The default max byte size for files of this type.
     */
    @Expose
    private final long maxFileSize;

    @Expose
    private final List<DataCategory> categories;

    @Expose
    private final long projectCount;

    public DataProjectType (ProjectTypeRecord rs) {

        this(rs, null);
    }

    public DataProjectType (ProjectTypeRecord rs, List<DataCategory> categories) {

        this.name = rs.getName();
        this.slug = rs.getSlug();
        this.gameSlug = rs.getGameSlug();
        this.maxFileSize = rs.getMaxFileSize();
        this.projectCount = rs.getProjectCount();

        this.categories = categories;
    }
}