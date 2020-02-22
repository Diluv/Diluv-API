package com.diluv.api.utils;

import java.io.IOException;

import com.diluv.api.DiluvAPI;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;

public class FormUtil {
    
    public static FormData getForm (HttpServerExchange exchange) {
        
        try {
            final FormDataParser parser = FormParserFactory.builder(false).addParsers(new FormEncodedDataDefinition()).build().createParser(exchange);
            return parser.parseBlocking();
        }
        catch (final IOException e) {
            e.printStackTrace();
            DiluvAPI.LOGGER.error("Failed to getForm.", e);
        }
        
        return null;
    }
    
    public static FormData getMultiPartForm (HttpServerExchange exchange) throws MultiPartParserDefinition.FileTooLargeException {
        
        return getMultiPartForm(exchange, 100000000);
    }
    
    public static FormData getMultiPartForm (HttpServerExchange exchange, long maxSize) throws MultiPartParserDefinition.FileTooLargeException {
        
        final MultiPartParserDefinition multiPartParserDefinition = new MultiPartParserDefinition();
        multiPartParserDefinition.setMaxIndividualFileSize(maxSize);
        
        try {
            final FormDataParser parser = FormParserFactory.builder(false).addParsers(new FormEncodedDataDefinition(), multiPartParserDefinition).build().createParser(exchange);
            return parser.parseBlocking();
        }
        catch (final MultiPartParserDefinition.FileTooLargeException e) {
            throw e;
        }
        catch (final IOException e) {
            e.printStackTrace();
            DiluvAPI.LOGGER.error("Failed to getForm.", e);
        }
        
        return null;
    }
}
