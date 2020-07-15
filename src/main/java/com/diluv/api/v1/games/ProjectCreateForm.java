package com.diluv.api.v1.games;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.FormParam;

public class ProjectCreateForm {

    @FormParam("name")
    public String name;

    @FormParam("summary")
    public String summary;

    @FormParam("description")
    public String description;

    @FormParam("tag1")
    private String tag1;

    @FormParam("tag2")
    private String tag2;

    @FormParam("tag3")
    private String tag3;

    @FormParam("tag4")
    private String tag4;

    @FormParam("logo")
    public InputStream logo;

    public Set<String> getTags () {

        Set<String> returned = new HashSet<>();
        returned.add(tag1);
        returned.add(tag2);
        returned.add(tag3);
        returned.add(tag4);
        return returned.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }
}