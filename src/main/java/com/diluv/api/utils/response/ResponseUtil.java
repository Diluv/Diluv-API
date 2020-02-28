package com.diluv.api.utils.response;

import javax.ws.rs.core.Response;

public final class ResponseUtil {

    public static Response successResponse (Object data) {

        return Response.ok(new DataResponse<>(data)).build();
    }
}