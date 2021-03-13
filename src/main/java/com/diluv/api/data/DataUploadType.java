package com.diluv.api.data;

import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Set;

public class DataUploadType {

    @Expose
    private final List<DataSlugName> loaders;

    @Expose
    private final Set<DataSlugName> releaseTypes;

    @Expose
    private final Set<String> classifiers;

    @Expose
    private final List<DataGameVersion> gameVersions;

    @Expose
    private final List<DataSlugName> filters;

    @Expose
    private final Set<DataSlugName> dependencyTypes;

    public DataUploadType (List<DataSlugName> loaders, Set<DataSlugName> releaseTypes, Set<String> classifiers, List<DataGameVersion> gameVersions, List<DataSlugName> filters, Set<DataSlugName> dependencyTypes) {

        this.loaders = loaders;
        this.releaseTypes = releaseTypes;
        this.classifiers = classifiers;
        this.gameVersions = gameVersions;
        this.filters = filters;
        this.dependencyTypes = dependencyTypes;
    }
}
