package com.diluv.api.v1.games;

import java.io.InputStream;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class ProjectFileUploadForm {

    @NotNull
    @FormParam("file")
    public InputStream file;

    @NotBlank
    @FormParam("filename")
    public String fileName;

    @Valid
    @NotNull
    @FormParam("data")
    @PartType(value = MediaType.APPLICATION_JSON)
    public ProjectFileUpload data;

}