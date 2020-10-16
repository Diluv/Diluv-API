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
import com.diluv.confluencia.database.record.GamesEntity;
import com.diluv.confluencia.database.record.ProjectRequestChangeEntity;
import com.diluv.confluencia.database.record.ProjectReviewEntity;
import com.diluv.confluencia.database.record.ProjectTypeLoadersEntity;
import com.diluv.confluencia.database.record.ProjectTypesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.TagsEntity;
import com.diluv.confluencia.database.record.UsersEntity;
import graphql.GraphQLException;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContext;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.schema.DataFetchingEnvironment;

public class Mutation implements GraphQLMutationResolver {

    public Game addGame (String slug, String name, String url, Part logoPNG, Part logoWebp, String projectTypeSlug, String projectTypeName) {

        return Confluencia.getTransaction(session -> {
            if (Confluencia.GAME.findOneBySlug(session, slug) != null) {
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
            projectType.setSlug(projectTypeSlug);
            projectType.setName(projectTypeName);

            game.addProjectType(projectType);
            game.setDefaultProjectTypeEntity(projectTypeSlug);
            session.save(game);

            return new Game(game);
        });
    }

    public Game updateGame (String slug, String name, String url, Part logoPNG, Part logoWebp) {

        return Confluencia.getTransaction(session -> {
            GamesEntity game = Confluencia.GAME.findOneBySlug(session, slug);

            if (game == null) {
                throw new GraphQLException("Game not found");
            }

            if (name != null) {
                game.setName(name);
            }

            if (url != null) {
                game.setUrl(url);
            }

            if (logoPNG != null || logoWebp != null) {
                File logoPath = new File(Constants.CDN_FOLDER, "games/" + game.getSlug());
                logoPath.mkdirs();
                try {
                    if (logoPNG != null) {
                        try (FileOutputStream fosPNG = new FileOutputStream(new File(logoPath, "logo.png"))) {
                            IOUtils.copy(logoPNG.getInputStream(), fosPNG);
                        }
                    }
                    if (logoWebp != null) {
                        try (FileOutputStream fosWebp = new FileOutputStream(new File(logoPath, "logo.webp"))) {
                            IOUtils.copy(logoWebp.getInputStream(), fosWebp);
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    throw new GraphQLException("Failed to create game logo's");
                }
            }

            session.update(game);

            return new Game(game);
        });
    }

    public ProjectType addProjectType (String gameSlug, String projectTypeSlug, String projectTypeName, boolean isDefault, Long maxFileSize) {

        return Confluencia.getTransaction(session -> {
            GamesEntity game = Confluencia.GAME.findOneBySlug(session, gameSlug);
            if (game == null) {
                throw new GraphQLException("Game doesn't exists");
            }

            ProjectTypesEntity projectType = new ProjectTypesEntity();
            projectType.setSlug(projectTypeSlug);
            projectType.setName(projectTypeName);
            if (maxFileSize == null) {
                projectType.setMaxFileSize(25000000L);
            }
            else {
                projectType.setMaxFileSize(maxFileSize);
            }
            game.addProjectType(projectType);

            if (isDefault) {
                game.setDefaultProjectTypeEntity(projectTypeSlug);
            }

            session.update(game);

            return new ProjectType(projectType);
        });
    }

    public ProjectType updateProjectType (String gameSlug, String projectTypeSlug, String projectTypeName, Boolean isDefault, Long maxFileSize) {

        return Confluencia.getTransaction(session -> {
            ProjectTypesEntity projectType = Confluencia.PROJECT.findOneProjectTypeByGameSlugAndProjectTypeSlug(session, gameSlug, projectTypeSlug);
            if (projectType == null) {
                throw new GraphQLException("Project Type doesn't exists");
            }

            if (projectTypeName != null) {
                projectType.setName(projectTypeName);
            }

            if (Boolean.TRUE.equals(isDefault)) {
                projectType.getGame().setDefaultProjectTypeEntity(projectTypeSlug);
                session.update(projectType.getGame());
            }

            if (maxFileSize != null) {
                projectType.setMaxFileSize(maxFileSize);
            }

            session.update(projectType);

            return new ProjectType(projectType);
        });
    }

    public Project reviewed (long projectId, boolean requestChange, String reason, DataFetchingEnvironment env) {

        return Confluencia.getTransaction(session -> {
            ProjectsEntity project = Confluencia.PROJECT.findOneProjectByProjectId(session, projectId);
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

            session.update(project);

            return new Project(project);
        });
    }

    public ProjectType addTag (String gameSlug, String projectTypeSlug, String tagSlug, String tagName) {

        return Confluencia.getTransaction(session -> {
            ProjectTypesEntity projectType = Confluencia.PROJECT.findOneProjectTypeByGameSlugAndProjectTypeSlug(session, gameSlug, projectTypeSlug);
            if (projectType == null) {
                throw new GraphQLException("Project Type doesn't exists");
            }

            TagsEntity tag = new TagsEntity(tagSlug, tagName);
            tag.setProjectType(projectType);
            session.save(tag);

            return new ProjectType(projectType);
        });
    }

    public ProjectType addLoader (String gameSlug, String projectTypeSlug, String loaderSlug, String loaderName) {

        return Confluencia.getTransaction(session -> {

            ProjectTypesEntity projectType = Confluencia.PROJECT.findOneProjectTypeByGameSlugAndProjectTypeSlug(session, gameSlug, projectTypeSlug);
            if (projectType == null) {
                throw new GraphQLException("Project Type doesn't exists");
            }

            ProjectTypeLoadersEntity tag = new ProjectTypeLoadersEntity(loaderSlug, loaderName);
            tag.setProjectType(projectType);
            session.save(tag);

            return new ProjectType(projectType);
        });
    }
}