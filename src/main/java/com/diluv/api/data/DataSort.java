package com.diluv.api.data;

import java.util.List;

import com.google.gson.annotations.Expose;

public class DataSort {

    @Expose
    private final List<String> games;

    @Expose
    private final List<String> news;

    @Expose
    private final List<String> projects;

    @Expose
    private final List<String> projectFiles;

    public DataSort (List<String> games, List<String> news, List<String> projects, List<String> projectFiles) {

        this.games = games;
        this.news = news;
        this.projects = projects;
        this.projectFiles = projectFiles;
    }
}
