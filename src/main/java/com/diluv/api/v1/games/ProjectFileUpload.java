package com.diluv.api.v1.games;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.google.gson.annotations.Expose;

public class ProjectFileUpload {

    @NotBlank(message = "PROJECT_FILE_INVALID_VERSION")
    @Size(max = 20, message = "PROJECT_FILE_INVALID_VERSION")
    @Expose
    public String version;

    // Null or <= 2000
    @Size(max = 2000, message = "PROJECT_FILE_INVALID_CHANGELOG")
    @Expose
    public String changelog;

    @NotBlank(message = "PROJECT_FILE_INVALID_RELEASE_TYPE")
    @Expose
    public String releaseType;

    @NotBlank(message = "PROJECT_FILE_INVALID_CLASSIFIER")
    @Expose
    public String classifier;

    @Expose
    public Set<String> gameVersions = new HashSet<>();

    @Expose
    public Set<String> loaders = new HashSet<>();

    @Expose
    public List<FileDependency> dependencies = new ArrayList<>();
}
