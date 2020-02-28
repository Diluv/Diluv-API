package com.diluv.api.v1;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.auth.ParamConverterAccessToken;

@Provider
public class ParameterProviderV1 implements ParamConverterProvider {

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter (Class<T> rawType, Type genericType, Annotation[] annotations) {
        
        if (rawType == AccessToken.class) {
            
            return ParamConverterAccessToken.INSTANCE;
        }
        
        return null;
    }  
}