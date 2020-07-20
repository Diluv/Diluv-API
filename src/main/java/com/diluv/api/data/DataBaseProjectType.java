package com.diluv.api.data;

import com.diluv.confluencia.database.record.ProjectTypesEntity;
import com.google.gson.annotations.Expose;

/**
 * Represents a supported project type for a supported game.
 */
public class DataBaseProjectType {

    /**
     * The slug for the project type.
     */
    @Expose
    private final String slug;

    /**
     * The display name for the project type.
     */
    @Expose
    private final String name;

    public DataBaseProjectType (ProjectTypesEntity rs) {

        this(rs.getSlug(), rs.getName());
    }

    public DataBaseProjectType (String slug, String name) {

        this.slug = slug;
        this.name = name;
    }
}