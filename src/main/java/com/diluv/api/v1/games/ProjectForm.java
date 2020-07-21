package com.diluv.api.v1.games;

import java.io.InputStream;

import javax.ws.rs.FormParam;

public class ProjectForm {

    @FormParam("name")
    public String name;

    @FormParam("summary")
    public String summary;

    @FormParam("description")
    public String description;

    @FormParam("tags")
    private String tags;

    @FormParam("logo")
    public InputStream logo;

    public String[] getTags () {

        if (this.tags == null) {
            return new String[0];
        }
        return this.tags.split(",");
    }
}