package com.diluv.api.graphql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;

import com.diluv.api.utils.Constants;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.GameDefaultProjectTypeEntity;
import com.diluv.confluencia.database.record.GamesEntity;
import com.diluv.confluencia.database.record.ProjectRequestChangeEntity;
import com.diluv.confluencia.database.record.ProjectReviewEntity;
import com.diluv.confluencia.database.record.ProjectTypesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.UsersEntity;
import graphql.GraphQLException;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContext;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.schema.DataFetchingEnvironment;

public class Mutation implements GraphQLMutationResolver {

    public Game addGame (String slug, String name, String url, Part logoPNG, Part logoWebp, String projectTypeSlug, String projectTypeName) {

        if (Confluencia.GAME.findOneBySlug(slug) != null) {
            throw new GraphQLException("Game already exists");
        }
        File logoPath = new File(Constants.CDN_FOLDER, "games/" + slug);
        logoPath.mkdirs();
        try {
            try (FileOutputStream fosPNG = new FileOutputStream(new File(logoPath, "logo.png"))) {
                IOUtils.copy(logoPNG.getInputStream(), fosPNG);
            }
            try (FileOutputStream fosWebp = new FileOutputStream(new File(logoPath, "logo.webp"))) {
                IOUtils.copy(logoWebp.getInputStream(), fosWebp);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new GraphQLException("Failed to create game logo's");
        }

        GamesEntity game = new GamesEntity();
        game.setSlug(slug);
        game.setName(name);
        game.setUrl(url);

        ProjectTypesEntity projectType = new ProjectTypesEntity();
        projectType.setGame(game);
        projectType.setSlug(projectTypeSlug);
        projectType.setName(projectTypeName);

        if (!Confluencia.GAME.insertGame(game)) {
            throw new GraphQLException("Failed to insert game");
        }

        if (!Confluencia.GAME.insertProjectType(projectType)) {
            throw new GraphQLException("Failed to insert project type");
        }

        if (!Confluencia.GAME.insertDefaultProjectType(new GameDefaultProjectTypeEntity(game, projectTypeSlug))) {
            throw new GraphQLException("Failed to insert default project type");
        }

        return new Game(game);
    }

    public ProjectType addProjectType (String gameSlug, String projectTypeSlug, String projectTypeName, boolean isDefault) {

        GamesEntity game = Confluencia.GAME.findOneBySlug(gameSlug);
        if (game == null) {
            throw new GraphQLException("Game doesn't exists");
        }

        ProjectTypesEntity projectType = new ProjectTypesEntity();
        projectType.setSlug(projectTypeSlug);
        projectType.setName(projectTypeName);
        projectType.setMaxFileSize(25000000L);

        game.addProjectType(projectType);

        if (isDefault) {
           game.setDefaultProjectTypeEntity(projectTypeSlug);
        }

        if (!Confluencia.GAME.updateGame(game)) {
            throw new GraphQLException("Failed to update game");
        }

        return new ProjectType(projectType);
    }

    public Project reviewed (long projectId, boolean requestChange, String reason, DataFetchingEnvironment env) {

        ProjectsEntity project = Confluencia.PROJECT.findOneProjectByProjectId(projectId);
        if (project == null) {
            throw new GraphQLException("Project doesn't exists");
        }

        DefaultGraphQLServletContext context = env.getContext();
        Token token = JWTUtil.getToken(context.getHttpServletRequest().getHeader("Authorization"));

        ProjectReviewEntity review = new ProjectReviewEntity();
        review.setReviewedBy(new UsersEntity(token.getUserId()));
        if (requestChange) {
            ProjectRequestChangeEntity requestChangeEntity = new ProjectRequestChangeEntity();
            requestChangeEntity.setReason(reason);
            review.setProjectRequestChange(requestChangeEntity);
        }
        else {
            project.setReleased(true);
            project.setReview(true);
        }

        project.addReview(review);

        if (!Confluencia.PROJECT.updateProject(project)) {
            throw new GraphQLException("Failed to update project");
        }

        return new Project(project);
    }
}