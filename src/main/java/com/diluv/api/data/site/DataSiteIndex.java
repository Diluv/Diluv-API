package com.diluv.api.data.site;

import java.util.List;

import com.google.gson.annotations.Expose;

public class DataSiteIndex {

    @Expose
    private final List<DataSiteGame> featuredGames;

    @Expose
    private final long projectCount;

    @Expose
    private final long authorCount;

    @Expose
    private final long gameCount;

    @Expose
    private final long projectTypeCount;

    public DataSiteIndex (List<DataSiteGame> featuredGames, long projectCount, long authorCount, long gameCount, long projectTypeCount) {

        this.featuredGames = featuredGames;
        this.projectCount = projectCount;
        this.authorCount = authorCount;
        this.gameCount = gameCount;
        this.projectTypeCount = projectTypeCount;
    }
}
