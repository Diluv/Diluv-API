package com.diluv.api.data;

import java.util.List;

import com.google.gson.annotations.Expose;

public class DataGameList {
    @Expose
    private final List<DataBaseGame> games;

    @Expose
    private final List<DataSort> sort;

    @Expose
    private final long gameCount;

    public DataGameList (List<DataBaseGame> games, List<DataSort> sort, long gameCount) {

        this.games = games;
        this.sort = sort;
        this.gameCount = gameCount;
    }
}
