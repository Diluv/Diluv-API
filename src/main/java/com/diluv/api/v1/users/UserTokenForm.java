package com.diluv.api.v1.users;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.validation.Valid;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

public class UserTokenForm {

    @Valid
    @FormParam("data")
    @PartType(value = MediaType.APPLICATION_JSON)
    public UserToken data;
}
