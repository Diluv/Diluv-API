package com.diluv.api.v1;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor;

import com.diluv.api.provider.CustomAtomFeedProvider;
import com.diluv.api.provider.GenericExceptionMapper;
import com.diluv.api.provider.GsonProvider;
import com.diluv.api.provider.NotFoundExceptionMapper;
import com.diluv.api.provider.ParameterProviderV1;
import com.diluv.api.provider.ResponseExceptionMapper;
import com.diluv.api.provider.ValidationExceptionMapper;
import com.diluv.api.utils.Constants;
import com.diluv.api.v1.admin.AdminAPI;
import com.diluv.api.v1.featured.FeaturedAPI;
import com.diluv.api.v1.games.GamesAPI;
import com.diluv.api.v1.internal.InternalAPI;
import com.diluv.api.v1.news.NewsAPI;
import com.diluv.api.v1.notifications.NotificationsAPI;
import com.diluv.api.v1.projects.ProjectsAPI;
import com.diluv.api.v1.site.SiteAPI;
import com.diluv.api.v1.users.UsersAPI;

@ApplicationPath("v1")
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
        classes.add(ResponseExceptionMapper.class);
        classes.add(ValidationExceptionMapper.class);

        classes.add(AdminAPI.class);
        classes.add(FeaturedAPI.class);
        classes.add(GamesAPI.class);
        classes.add(InternalAPI.class);
        classes.add(NewsAPI.class);
        classes.add(NotificationsAPI.class);
        classes.add(ProjectsAPI.class);
        classes.add(SiteAPI.class);
        classes.add(UsersAPI.class);
        classes.add(GenericAPI.class);

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