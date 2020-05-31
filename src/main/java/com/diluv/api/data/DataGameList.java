package com.diluv.api.data;

import java.util.List;

import com.google.gson.annotations.Expose;

public class DataGameList {
    @Expose
    private final List<DataGame> games;

    @Expose
    private final List<String> sort;

    public DataGameList (List<DataGame> games, List<String> sort) {

        this.games = games;
        this.sort = sort;
    }
}
