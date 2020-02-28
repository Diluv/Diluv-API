package com.diluv.api.v1.games;

import java.io.InputStream;

import javax.ws.rs.FormParam;

public class ProjectFileUploadForm {
    
    @FormParam("changelog")
    public String changelog;
    
    @FormParam("file")
    public InputStream file;
    
    @FormParam("filename")
    public String fileName;
}