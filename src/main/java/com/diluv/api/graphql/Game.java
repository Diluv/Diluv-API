package com.diluv.api.graphql;

import com.diluv.confluencia.database.record.GamesEntity;

public class Game {

    private String slug;
    private String name;
    private String defaultProjectType;
    private GamesEntity entity;

    public Game (GamesEntity entity) {

        this.slug = entity.getSlug();
        this.name = entity.getName();
        this.defaultProjectType = entity.getDefaultProjectTypeSlug();
        this.entity = entity;
    }

    public GamesEntity getEntity () {

        return this.entity;
    }
}
