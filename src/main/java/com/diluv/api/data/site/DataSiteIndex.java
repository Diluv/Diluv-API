package com.diluv.api.data.site;

import java.util.List;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataGame;
import com.google.gson.annotations.Expose;

public class DataSiteIndex {

    @Expose
    private final List<DataGame> featuredGames;

    @Expose
    private final List<DataBaseProject> featuredProjects;

    @Expose
    private final long projectCount;

    @Expose
    private final long contributorCount;

    public DataSiteIndex (List<DataGame> featuredGames, List<DataBaseProject> featuredProjects, long projectCount, long contributorCount) {

        this.featuredGames = featuredGames;
        this.featuredProjects = featuredProjects;
        this.projectCount = projectCount;
        this.contributorCount = contributorCount;
    }
}
