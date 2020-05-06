package com.diluv.api.v1;

import com.diluv.api.provider.*;
import com.diluv.api.utils.Constants;
import com.diluv.api.v1.auth.AuthAPI;
import com.diluv.api.v1.featured.FeaturedAPI;
import com.diluv.api.v1.games.GamesAPI;
import com.diluv.api.v1.news.NewsAPI;
import com.diluv.api.v1.projects.ProjectsAPI;
import com.diluv.api.v1.users.UsersAPI;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor;

import javax.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

public class APIV1 extends Application {

    @Override
    public Set<Class<?>> getClasses () {

        final Set<Class<?>> classes = new HashSet<>();

        // Enables Json
        classes.add(GsonProvider.class);

        // Enables XML
        classes.add(CustomAtomFeedProvider.class);

        // Enables custom param types
        classes.add(ParameterProviderV1.class);

        // Enables gzip
        classes.add(GZIPEncodingInterceptor.class);

        // Enables 404 handler
        classes.add(NotFoundExceptionMapper.class);

        // Enables exception handler
        classes.add(GenericExceptionMapper.class);

        classes.add(AuthAPI.class);
        classes.add(FeaturedAPI.class);
        classes.add(GamesAPI.class);
        classes.add(ProjectsAPI.class);
        classes.add(NewsAPI.class);
        classes.add(UsersAPI.class);

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