package com.diluv.api.endpoints.v1.game.domain;

import com.diluv.confluencia.database.record.ProjectTypeRecord;
import com.fasterxml.jackson.annotation.JsonCreator;

public class ProjectTypeDomain {
    private final String name;
    private final String slug;
    private final String gameSlug;

    @JsonCreator
    public ProjectTypeDomain (ProjectTypeRecord rs) {

        this.name = rs.getName();
        this.slug = rs.getSlug();
        this.gameSlug = rs.getGameSlug();
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
}
