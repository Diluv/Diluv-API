package com.diluv.api.data;

import com.google.gson.annotations.Expose;

public class DataImage {

    @Expose
    private final DataImageSource fallback;

    @Expose
    private final DataImageSource[] sources;

    public DataImage (DataImageSource fallback, DataImageSource[] sources) {

        this.fallback = fallback;
        this.sources = sources;
    }
}
