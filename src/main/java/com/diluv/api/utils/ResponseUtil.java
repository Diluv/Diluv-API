package com.diluv.api.utils;

import javax.ws.rs.core.Response;

import com.diluv.api.endpoints.v1.DataResponse;

public final class ResponseUtil {

    public static Response successResponse (Object data) {

        return Response.ok(new DataResponse<>(data)).build();
    }
}