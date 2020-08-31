package com.diluv.api.v1.games;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class ProjectCreate {

    @Expose
    public String name;

    @Expose
    public String summary;

    @Expose
    public String description;

    @Expose
    public List<String> tags = new ArrayList<>();
}
