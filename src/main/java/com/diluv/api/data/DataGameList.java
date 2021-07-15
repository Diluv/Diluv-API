package com.diluv.api.data;

import java.util.List;

import com.google.gson.annotations.Expose;

public class DataGameList {

    @Expose
    private final List<DataSlugName> games;

    @Expose
    private final long gameCount;

    public DataGameList (List<DataSlugName> games, long gameCount) {

        this.games = games;
        this.gameCount = gameCount;
    }
}
