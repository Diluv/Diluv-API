package com.diluv.api.v1.games;

import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class ProjectFileUploadForm {

    @FormParam("file")
    public InputStream file;

    @FormParam("filename")
    public String fileName;

    @FormParam("data")
    @PartType(value = MediaType.APPLICATION_JSON)
    public ProjectFileUpload data;

}