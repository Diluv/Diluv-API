package com.diluv.api.graphql.data;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.database.record.GamesEntity;

public class Game {

    private String slug;
    private String name;
    private String defaultProjectType;
    private String url;
    private String createdAt;
    private Image logo;
    private GamesEntity entity;

    public Game (GamesEntity entity) {

        this.slug = entity.getSlug();
        this.name = entity.getName();
        this.defaultProjectType = entity.getDefaultProjectTypeSlug();
        this.url = entity.getUrl();
        this.createdAt = entity.getCreatedAt().toString();
        this.logo = new Image(Constants.getGameLogoURL(entity.getSlug()));
        this.entity = entity;
    }

    public GamesEntity getEntity () {

        return this.entity;
    }
}
