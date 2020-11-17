package com.diluv.api.data;

import java.util.List;

import com.google.gson.annotations.Expose;

public class DataNewsPosts {

    /**
     * A list of posts
     */
    @Expose
    private final List<DataNewsPost> posts;

    public DataNewsPosts (List<DataNewsPost> posts) {

        this.posts = posts;
    }
}
