package com.diluv.api.data;

import com.google.gson.annotations.Expose;

public class DataImageSource {

    @Expose
    private final String src;

    @Expose
    private final String type;

    public DataImageSource (String src, String type) {

        this.src = src;
        this.type = type;
    }

    public String getSrc () {

        return src;
    }

    public String getType () {

        return type;
    }
}
