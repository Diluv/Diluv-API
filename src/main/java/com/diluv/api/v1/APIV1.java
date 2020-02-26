package com.diluv.api.v1;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

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

        classes.add(AuthAPI.class);
        classes.add(GamesAPI.class);
        classes.add(UsersAPI.class);
        classes.add(NewsAPI.class);

        return classes;
    }
}