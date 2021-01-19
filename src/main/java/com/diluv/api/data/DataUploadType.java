package com.diluv.api.data;

import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class DataUploadType {

    @Expose
    private final List<DataSlugName> loaders;

    @Expose
    private final Set<String> releaseTypes;

    @Expose
    private final Set<String> classifiers;

    public DataUploadType (List<DataSlugName> loaders, Set<String> releaseTypes, Set<String> classifiers) {

        this.loaders = loaders;
        this.releaseTypes = releaseTypes;
        this.classifiers = classifiers;
    }
}
