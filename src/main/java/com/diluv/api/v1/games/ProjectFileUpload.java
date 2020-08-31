package com.diluv.api.v1.games;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class ProjectFileUpload {

    @Expose
    public String version;

    @Expose
    public String changelog;

    @Expose
    public String releaseType;

    @Expose
    public String classifier;

    @Expose
    public List<String> gameVersions = new ArrayList<>();

    @Expose
    public List<FileDependency> dependencies = new ArrayList<>();
}
