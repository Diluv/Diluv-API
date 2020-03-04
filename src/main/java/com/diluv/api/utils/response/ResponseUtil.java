package com.diluv.api.utils.response;

import java.util.List;

import javax.ws.rs.core.Response;

import com.diluv.api.data.DataPagination;

public final class ResponseUtil {

    public static Response successResponse (Object data) {

        return Response.ok(new DataResponse<>(data)).build();
    }

    public static Response successResponsePagination (List<?> data, String cursor) {

        return Response.ok(new DataPagination<>(data, cursor)).build();
    }
}