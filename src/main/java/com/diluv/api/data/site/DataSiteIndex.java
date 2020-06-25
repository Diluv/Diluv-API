package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataGame;
import com.google.gson.annotations.Expose;

public class DataSiteIndex {

    @Expose
    private final List<DataSiteGame> featuredGames;

    @Expose
    private final long projectCount;

    @Expose
    private final long contributorCount;

    @Expose
    private final long gameCount;

    @Expose
    private final long projectTypeCount;

    public DataSiteIndex (List<DataSiteGame> featuredGames, long projectCount, long contributorCount, long gameCount, long projectTypeCount) {

        this.featuredGames = featuredGames;
        this.projectCount = projectCount;
        this.contributorCount = contributorCount;
        this.gameCount = gameCount;
        this.projectTypeCount = projectTypeCount;
    }
}
