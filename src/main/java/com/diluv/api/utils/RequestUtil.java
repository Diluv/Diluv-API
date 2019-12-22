package com.diluv.api.utils;

import java.util.Deque;
import java.util.logging.Logger;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;

// TODO Temp class name
public class RequestUtil {

    private static final Logger LOGGER = Logger.getLogger(RequestUtil.class.getName());

    private RequestUtil () {

    }

    public static FormData.FileItem getFormFile (final FormData data, String paramName) {

        Deque<FormData.FormValue> param = data.get(paramName);

        if (param == null) {
            return null;
        }
        FormData.FormValue formValue = param.peekFirst();
        if (formValue == null) {
            return null;
        }
        if (!formValue.isFileItem()) {
            return null;
        }
        return formValue.getFileItem();
    }

    public static String getFormParam (final FormData data, String paramName) {

        Deque<FormData.FormValue> param = data.get(paramName);

        if (param == null) {
            return null;
        }
        FormData.FormValue formValue = param.peekFirst();
        if (formValue == null) {
            return null;
        }
        return formValue.getValue();
    }

    public static String getParam (final HttpServerExchange exchange, String paramName) {

        Deque<String> param = exchange.getQueryParameters().get(paramName);

        if (param == null) {
            return null;
        }
        return param.peek();
    }
}