package com.diluv.api.v1.games;

import java.io.InputStream;

import javax.ws.rs.FormParam;

public class ProjectCreateForm {
    
    @FormParam("name")
    public String name;

    @FormParam("summary")
    public String summary;

    @FormParam("description")
    public String description;

    @FormParam("logo")
    public InputStream logo;
}