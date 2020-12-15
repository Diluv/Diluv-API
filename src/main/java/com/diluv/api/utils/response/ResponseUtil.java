package com.diluv.api.utils.response;

import javax.ws.rs.core.Response;

public final class ResponseUtil {

    public static Response successResponse (Object data) {

        return Response.ok(data).build();
    }

    public static Response noContent () {

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}