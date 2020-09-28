package com.diluv.api.graphql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import graphql.kickstart.tools.GraphQLResolver;

public class ProjectResolver implements GraphQLResolver<Project> {

    public List<Author> authors (Project project) {

        List<Author> authors = new ArrayList<>();
        authors.add(new Author(new User(project.getEntity().getOwner()), "owner"));
        authors.addAll(project.getEntity().getAuthors().stream().map(a -> new Author(new User(a.getUser()), a.getRole())).collect(Collectors.toList()));
        return authors;
    }

    public ProjectType projectType (Project project) {

        return new ProjectType(project.getEntity().getProjectType());
    }
}
