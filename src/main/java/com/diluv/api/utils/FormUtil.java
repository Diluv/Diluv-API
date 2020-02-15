package com.diluv.api.utils;

import java.io.IOException;

import com.diluv.api.DiluvAPI;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;

public class FormUtil {

    public static FormData getForm (HttpServerExchange exchange) {

        try (FormDataParser parser = FormParserFactory.builder().build().createParser(exchange)) {

            return parser.parseBlocking();
        }
        catch (IOException e) {
            e.printStackTrace();
            DiluvAPI.LOGGER.error("Failed to getForm.", e);
        }

        return null;
    }
}
