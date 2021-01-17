package com.diluv.api.graphql;

import com.diluv.api.graphql.data.Loader;
import com.diluv.api.graphql.data.ProjectType;
import graphql.kickstart.tools.GraphQLResolver;

public class LoaderResolver implements GraphQLResolver<Loader> {

    public ProjectType projectType (Loader loader) {

        return new ProjectType(loader.getEntity().getProjectType());
    }
}
