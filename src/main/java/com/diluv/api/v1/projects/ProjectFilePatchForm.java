package com.diluv.api.v1.projects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class ProjectFilePatchForm {

    @Valid
    @NotNull
    @FormParam("data")
    @PartType(value = MediaType.APPLICATION_JSON)
    public ProjectFilePatch data;
}
