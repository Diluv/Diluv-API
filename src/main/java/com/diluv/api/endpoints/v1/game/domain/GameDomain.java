package com.diluv.api.endpoints.v1.game.domain;

import com.diluv.api.database.record.GameRecord;

public class GameDomain {
    private final String slug;
    private final String name;
    private final String url;

    public GameDomain (GameRecord gameRecord) {

        this.slug = gameRecord.getSlug();
        this.name = gameRecord.getName();
        this.url = gameRecord.getUrl();
    }

    public String getSlug () {

        return this.slug;
    }

    public String getName () {

        return this.name;
    }

    public String getUrl () {

        return this.url;
    }
}
