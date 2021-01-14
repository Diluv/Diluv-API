package com.diluv.api.v1.games;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

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
    public Set<String> gameVersions = new HashSet<>();

    @Expose
    public Set<String> loaders = new HashSet<>();

    @Expose
    public List<FileDependency> dependencies = new ArrayList<>();
}
