package com.diluv.api.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

    @Override
    public boolean isReadable (Class<?> type, Type genericType, java.lang.annotation.Annotation[] annotations, MediaType mediaType) {

        return true;
    }

    @Override
    public Object readFrom (Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {

        try (InputStreamReader reader = new InputStreamReader(entityStream, StandardCharsets.UTF_8)) {

            return this.getGsonInstance().fromJson(reader, genericType);
        }

        catch (final JsonSyntaxException e) {

            // TODO handle error
            return null;
        }
    }

    @Override
    public boolean isWriteable (Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return true;
    }

    @Override
    public void writeTo (Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {

        try (OutputStreamWriter out = new OutputStreamWriter(entityStream, StandardCharsets.UTF_8)) {

            this.getGsonInstance().toJson(object, out);
        }
    }

    private Gson gson;

    private Gson getGsonInstance () {

        if (this.gson == null) {

            this.gson = new GsonBuilder().create();
        }

        return this.gson;
    }
}