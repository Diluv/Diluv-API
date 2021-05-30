package com.diluv.api.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import com.diluv.api.utils.auth.ParamConverterAccessToken;
import com.diluv.api.utils.auth.tokens.Token;

@Provider
public class ParameterProviderV1 implements ParamConverterProvider {

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter (Class<T> rawType, Type genericType, Annotation[] annotations) {

        if (rawType == Token.class) {

            return (ParamConverter<T>) ParamConverterAccessToken.INSTANCE;
        }

        return null;
    }
}