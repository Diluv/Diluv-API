package com.diluv.api.graphql;

import java.util.List;
import java.util.stream.Collectors;

import graphql.kickstart.tools.GraphQLResolver;

public class ProjectTypeResolver implements GraphQLResolver<ProjectType> {

    public List<Tag> tags (ProjectType projectType) {

        return projectType.getEntity().getTags().stream().map(Tag::new).collect(Collectors.toList());
    }

    public List<Loader> loaders (ProjectType projectType) {

        return projectType.getEntity().getProjectTypeLoaders().stream().map(Loader::new).collect(Collectors.toList());
    }

    public Game game (ProjectType projectType) {

        return new Game(projectType.getEntity().getGame());
    }
}
