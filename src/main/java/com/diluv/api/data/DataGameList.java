package com.diluv.api.data;

import java.util.List;

import com.google.gson.annotations.Expose;

public class DataGameList {

    @Expose
    private final List<DataSlugName> games;

    @Expose
    private final List<DataSlugName> sort;

    @Expose
    private final long gameCount;

    public DataGameList (List<DataSlugName> games, List<DataSlugName> sort, long gameCount) {

        this.games = games;
        this.sort = sort;
        this.gameCount = gameCount;
    }
}
