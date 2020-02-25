package com.diluv.api2.v1;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.diluv.api2.util.GsonProvider;
import com.diluv.api2.v1.user.UserAPI;

public class APIV1 extends Application {
    
    @Override
    public Set<Class<?>> getClasses () {
        
        final Set<Class<?>> classes = new HashSet<>();
        
        // Enables Json
        classes.add(GsonProvider.class);
        
        classes.add(UserAPI.class);
        
        return classes;
    }
}