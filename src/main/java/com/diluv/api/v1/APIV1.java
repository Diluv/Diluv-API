package com.diluv.api.v1;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor;

import com.diluv.api.utils.Constants;
import com.diluv.api.utils.GsonProvider;
import com.diluv.api.v1.auth.AuthAPI;
import com.diluv.api.v1.games.GamesAPI;
import com.diluv.api.v1.news.NewsAPI;
import com.diluv.api.v1.users.UsersAPI;

public class APIV1 extends Application {

    @Override
    public Set<Class<?>> getClasses () {

        final Set<Class<?>> classes = new HashSet<>();

        // Enables Json
        classes.add(GsonProvider.class);

        // Enables custom param types
        classes.add(ParameterProviderV1.class);

        // Enables gzip
        classes.add(GZIPEncodingInterceptor.class);


        classes.add(AuthAPI.class);
        classes.add(GamesAPI.class);
        classes.add(UsersAPI.class);
        classes.add(NewsAPI.class);

        return classes;
    }

    @Override
    public Set<Object> getSingletons () {

        final Set<Object> singletons = new HashSet<>();
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.getAllowedOrigins().addAll(Constants.ALLOWED_ORIGINS);
        singletons.add(corsFilter);
        return singletons;
    }
}