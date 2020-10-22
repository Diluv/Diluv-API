package com.diluv.api.v1.admin;

import com.google.gson.annotations.Expose;

public class AdminGameData {

    @Expose
    public String slug;

    @Expose
    public String name;

    @Expose
    public String url;

    @Expose
    public String projectTypeSlug;

    @Expose
    public String projectTypeName;
}
