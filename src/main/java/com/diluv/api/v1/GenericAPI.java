package com.diluv.api.v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

import com.diluv.api.utils.response.ResponseUtil;

@GZIP
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class GenericAPI {

    @GET
    @Path("/ping")
    public Response ping () {

        return ResponseUtil.successResponse(null);
    }
}
