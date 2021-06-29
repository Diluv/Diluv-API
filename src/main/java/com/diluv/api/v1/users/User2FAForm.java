package com.diluv.api.v1.users;

import javax.validation.Valid;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class User2FAForm {

    @Valid
    @FormParam("data")
    @PartType(value = MediaType.APPLICATION_JSON)
    public User2FA data;
}
