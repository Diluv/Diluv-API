package com.diluv.api.endpoints.v1.game.domain;

import com.diluv.confluencia.database.record.ProjectTypeRecord;

public class ProjectTypeDomain {
    private final String name;
    private final String slug;
    private final String gameSlug;
    private final long maxSize;

    public ProjectTypeDomain (ProjectTypeRecord rs) {

        this.name = rs.getName();
        this.slug = rs.getSlug();
        this.gameSlug = rs.getGameSlug();
        this.maxSize = rs.getMaxSize();
    }

    public String getName () {

        return this.name;
    }

    public String getSlug () {

        return this.slug;
    }

    public String getGameSlug () {

        return this.gameSlug;
    }

    public long getMaxSize () {

        return this.maxSize;
    }
}
