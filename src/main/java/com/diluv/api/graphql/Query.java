package com.diluv.api.graphql;

import java.util.List;
import java.util.stream.Collectors;

import com.diluv.api.graphql.data.Game;
import com.diluv.api.graphql.data.Project;
import com.diluv.api.graphql.data.ProjectFile;
import com.diluv.api.graphql.data.ProjectType;
import com.diluv.api.graphql.data.RegistrationCodes;
import com.diluv.api.graphql.data.Stats;
import com.diluv.api.utils.query.PaginationQuery;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.sort.ProjectSort;
import com.diluv.confluencia.database.sort.Sort;
import graphql.GraphQLContext;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;

public class Query implements GraphQLQueryResolver {

    public List<Game> games (int limit, long page, String sort) {

        return Confluencia.getTransaction(session -> {
            return Confluencia.GAME.findAll(session, page, limit, getSortOrDefault(sort, ProjectSort.LIST, ProjectSort.NEW), "").stream().map(Game::new).collect(Collectors.toList());
        });
    }

    public Game game (String gameSlug) {

        return Confluencia.getTransaction(session -> {
            return new Game(Confluencia.GAME.findOneBySlug(session, gameSlug));
        });
    }

    public ProjectType projectType (String gameSlug, String projectTypeSlug) {

        return Confluencia.getTransaction(session -> {
            return new ProjectType(Confluencia.PROJECT.findOneProjectTypeByGameSlugAndProjectTypeSlug(session, gameSlug, projectTypeSlug));
        });
    }

    public Project project (String gameSlug, String projectTypeSlug, String projectSlug) {

        return Confluencia.getTransaction(session -> {
            return new Project(Confluencia.PROJECT.findOneProject(session, gameSlug, projectTypeSlug, projectSlug));
        });
    }

    public Project projectById (long projectId) {

        return Confluencia.getTransaction(session -> {
            return new Project(Confluencia.PROJECT.findOneProjectByProjectId(session, projectId));
        });
    }

    public ProjectFile projectFile (long fileId) {

        return Confluencia.getTransaction(session -> {
            return new ProjectFile(Confluencia.FILE.findOneById(session, fileId));
        });
    }

    public List<ProjectType> projectTypes (String gameSlug) {

        return Confluencia.getTransaction(session -> {
            return Confluencia.GAME.findAllProjectTypesByGameSlug(session, gameSlug).stream().map(ProjectType::new).collect(Collectors.toList());
        });
    }

    public List<Project> projects (String gameSlug, String projectTypeSlug, int limit, long page, String sort) {

        long p = PaginationQuery.getPage(page);
        int l = PaginationQuery.getLimit(limit);
        Sort s = getSortOrDefault(sort, ProjectSort.LIST, ProjectSort.NEW);
        return Confluencia.getTransaction(session -> {
            return Confluencia.PROJECT.findAllByGameAndProjectType(session, gameSlug, projectTypeSlug, "", p, l, s).stream().map(Project::new).collect(Collectors.toList());
        });
    }

    public List<Project> projectReviews (int limit, long page) {

        long p = PaginationQuery.getPage(page);
        int l = PaginationQuery.getLimit(limit);
        return Confluencia.getTransaction(session -> {
            return Confluencia.PROJECT.findAllByReview(session, p, l).stream().map(Project::new).collect(Collectors.toList());
        });
    }

    public Stats stats () {

        return Confluencia.getTransaction(session -> {
            long gameCount = Confluencia.GAME.countAllBySearch(session, "");
            long projectCount = Confluencia.PROJECT.countAll(session, true);
            long unreleasedProjectCount = Confluencia.PROJECT.countAll(session, false);
            long userCount = Confluencia.USER.countAll(session);
            long tempUserCount = Confluencia.USER.countAllTempUsers(session);
            long fileSize = Confluencia.FILE.countAllFileSize(session);

            return new Stats(gameCount, projectCount, unreleasedProjectCount, userCount, tempUserCount, fileSize);
        });
    }

    public List<RegistrationCodes> registrationCodes (DataFetchingEnvironment env) {

        GraphQLContext context = env.getGraphQlContext();
        if (context == null) {
            //TODO ERROR
            return null;
        }
        long userId = context.get("userId");
        return Confluencia.getTransaction(session -> {
            return Confluencia.MISC.findAllRegistrationCodesByUser(session, userId).stream().map(RegistrationCodes::new).collect(Collectors.toList());
        });
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