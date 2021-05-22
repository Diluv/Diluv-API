package com.diluv.api.v1.games;

import java.io.InputStream;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class ProjectForm {

    @Valid
    @NotNull
    @FormParam("data")
    @PartType(value = MediaType.APPLICATION_JSON)
    public ProjectCreate data;

    @Valid
    @NotNull
    @FormParam("logo")
    public InputStream logo;

}