package com.diluv.api.v1.games;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.google.gson.annotations.Expose;

public class ProjectAuthors {

    @NotNull(message = "INVALID_USER_ID")
    @Expose
    public long userId;

    @Expose
    public List<String> permissions = new ArrayList<>();

    @NotNull(message = "INVALID_ROLE")
    @Expose
    public String role;
}
