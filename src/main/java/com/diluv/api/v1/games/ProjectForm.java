package com.diluv.api.v1.games;

import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class ProjectForm {

    @FormParam("data")
    @PartType(value = MediaType.APPLICATION_JSON)
    public ProjectCreate data;

    @FormParam("logo")
    public InputStream logo;

}