package com.diluv.api.graphql;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.sort.GameSort;
import com.diluv.confluencia.database.sort.ProjectSort;
import graphql.kickstart.tools.GraphQLQueryResolver;

public class Query implements GraphQLQueryResolver {

    public List<Game> games () {

        return Confluencia.GAME.findAll(1, 25, GameSort.NEW, "").stream().map(Game::new).collect(Collectors.toList());
    }

    public List<Project> projects (String gameSlug, String projectType) {

        return Confluencia.PROJECT.findAllByGameAndProjectType(gameSlug, projectType, "", 1, 25, ProjectSort.NEW).stream().map(Project::new).collect(Collectors.toList());
    }
}