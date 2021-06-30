package com.diluv.api.v1.projects;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.Size;

import com.diluv.api.v1.games.FileDependency;
import com.google.gson.annotations.Expose;

public class ProjectFilePatch {

    @Expose
    public String displayName;

    @Size(max = 20, message = "PROJECT_FILE_INVALID_VERSION")
    @Expose
    public String version;

    // Null or <= 2000
    @Size(max = 2000, message = "PROJECT_FILE_INVALID_CHANGELOG")
    @Expose
    public String changelog;

    @Expose
    public String releaseType;

    @Expose
    public String classifier;

    @Expose
    public Set<String> gameVersions;

    @Expose
    public Set<String> loaders;

    @Expose
    public List<FileDependency> dependencies;
}
