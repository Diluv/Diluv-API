package com.diluv.api.data;

import java.util.List;

import com.google.gson.annotations.Expose;

public class DataFeatured {

    @Expose
    private final List<DataGame> games;

    @Expose
    private final List<DataBaseProject> projects;

    @Expose
    private final long projectCount;

    @Expose
    private final long contributorCount;

    public DataFeatured (List<DataGame> games, List<DataBaseProject> projects, long projectCount,
                         long contributorCount) {

        this.games = games;
        this.projects = projects;
        this.projectCount = projectCount;
        this.contributorCount = contributorCount;
    }
}
