package com.diluv.api.graphql;

import graphql.kickstart.tools.GraphQLResolver;

public class TagResolver implements GraphQLResolver<Tag> {

    public ProjectType projectType (Tag tag) {

        return new ProjectType(tag.getEntity().getProjectType());
    }
}
