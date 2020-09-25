package com.diluv.api.graphql;

import com.diluv.api.utils.query.PaginationQuery;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.sort.GameSort;
import com.diluv.confluencia.database.sort.ProjectSort;
import com.diluv.confluencia.database.sort.Sort;
import graphql.kickstart.tools.GraphQLQueryResolver;

import java.util.List;
import java.util.stream.Collectors;

public class Query implements GraphQLQueryResolver {

    public List<Game> games () {

        return Confluencia.GAME.findAll(1, 25, GameSort.NEW, "").stream().map(Game::new).collect(Collectors.toList());
    }

    public List<Project> projects (String gameSlug, String projectTypeSlug, Long page, Integer limit, String sort) {

        long p = PaginationQuery.getPage(page);
        int l = PaginationQuery.getLimit(limit);
        Sort s = getSortOrDefault(sort, ProjectSort.LIST, ProjectSort.NEW);
        return Confluencia.PROJECT.findAllByGameAndProjectType(gameSlug, projectTypeSlug, "", p, l, s).stream().map(Project::new).collect(Collectors.toList());
    }

    public List<Project> projectReviews (Long page, Integer limit) {

        long p = PaginationQuery.getPage(page);
        int l = PaginationQuery.getLimit(limit);

        return Confluencia.PROJECT.findAllByReview(p, l).stream().map(Project::new).collect(Collectors.toList());
    }

    public Sort getSortOrDefault (String sort, List<Sort> sortList, Sort defaultSort) {

        for (Sort b : sortList) {
            if (b.getSlug().equalsIgnoreCase(sort)) {
                return b;
            }
        }
        return defaultSort;
    }
}