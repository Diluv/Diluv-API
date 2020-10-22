package com.diluv.api.v1.admin;

import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class AdminGameForm {

    @FormParam("data")
    @PartType(value = MediaType.APPLICATION_JSON)
    public AdminGameData data;

    @FormParam("logoPNG")
    public InputStream logoPNG;

    @FormParam("logoWebp")
    public InputStream logoWebp;
}
