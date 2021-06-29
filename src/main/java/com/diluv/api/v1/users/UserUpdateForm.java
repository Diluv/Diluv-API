package com.diluv.api.v1.users;

import javax.validation.Valid;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class UserUpdateForm {

    @Valid
    @FormParam("data")
    @PartType(value = MediaType.APPLICATION_JSON)
    public UserUpdate data;
}