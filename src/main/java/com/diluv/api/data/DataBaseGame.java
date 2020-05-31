package com.diluv.api.data;

import com.diluv.confluencia.database.record.GameRecord;
import com.google.gson.annotations.Expose;

/**
 * Represents the data for a game that we support.
 */
public class DataBaseGame {

    /**
     * The slug for the game. This is a unique string that identifies the game in URLs and API
     * requests.
     */
    @Expose
    private final String slug;

    /**
     * The display name for the game.
     */
    @Expose
    private final String name;

    public DataBaseGame (GameRecord rs) {

        this(rs.getSlug(), rs.getName());
    }

    public DataBaseGame (String slug, String name) {

        this.slug = slug;
        this.name = name;
    }
}