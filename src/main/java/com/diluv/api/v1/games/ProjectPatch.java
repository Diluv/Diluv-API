package com.diluv.api.v1.games;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import com.google.gson.annotations.Expose;

public class ProjectPatch {

    @Size(min = 5, max = 50, message = "PROJECT_INVALID_NAME")
    @Expose
    public String name;

    @Size(min = 10, max = 250, message = "PROJECT_INVALID_SUMMARY")
    @Expose
    public String summary;

    @Size(min = 50, max = 10000, message = "PROJECT_INVALID_DESCRIPTION")
    @Expose
    public String description;

    @Expose
    public List<String> tags = new ArrayList<>();
}
