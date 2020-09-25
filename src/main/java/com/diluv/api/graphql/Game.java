package com.diluv.api.graphql;

import com.diluv.confluencia.database.record.GamesEntity;

public class Game {

    private String slug;
    private String name;

    public Game (GamesEntity entity) {

        this.slug = entity.getSlug();
        this.name = entity.getName();
    }
}
