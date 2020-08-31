package com.diluv.api.v1.games;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

public class ProjectForm {

    @FormParam("data")
    @PartType(value = MediaType.APPLICATION_JSON)
    public ProjectCreate data;

    @FormParam("logo")
    public InputStream logo;

}