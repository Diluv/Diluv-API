package com.diluv.api.v1.games;

import javax.ws.rs.FormParam;

import java.io.InputStream;

public class ProjectFileUploadForm {

    @FormParam("changelog")
    public String changelog;

    @FormParam("file")
    public InputStream file;
}
