package com.diluv.api.graphql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.diluv.api.graphql.data.Project;
import com.diluv.api.graphql.data.ProjectType;
import com.diluv.api.graphql.data.RegistrationCodes;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.GamesEntity;
import com.diluv.confluencia.database.record.ProjectRequestChangeEntity;
import com.diluv.confluencia.database.record.ProjectReviewEntity;
import com.diluv.confluencia.database.record.ProjectTypeLoadersEntity;
import com.diluv.confluencia.database.record.ProjectTypesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.RegistrationCodesEntity;
import com.diluv.confluencia.database.record.TagsEntity;
import com.diluv.confluencia.database.record.UsersEntity;
import graphql.GraphQLContext;
import graphql.GraphQLException;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.schema.DataFetchingEnvironment;

public class Mutation implements GraphQLMutationResolver {

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

            //TODO
            GraphQLContext context = env.getGraphQlContext();
            if (context == null) {
                //TODO ERROR
                return null;
            }
            long userId = context.get("userId");

            ProjectReviewEntity review = new ProjectReviewEntity();
            review.setReviewedBy(new UsersEntity(userId));
            if (requestChange) {
                review.setProjectRequestChange(new ProjectRequestChangeEntity(reason));
            }
            else {
                project.setReleased(true);
                review.setCompleted(true);
            }

            project.setReview(false);
            project.addReview(review);

            session.update(project);

            //TODO EMAIL

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

    public List<RegistrationCodes> registrationCodes (int count, DataFetchingEnvironment env) {

        GraphQLContext context = env.getGraphQlContext();
        if (context == null) {
            //TODO ERROR
            return null;
        }
        long userId = context.get("userId");
        return Confluencia.getTransaction(session -> {
            List<RegistrationCodes> codes = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                RegistrationCodesEntity entity = new RegistrationCodesEntity(UUID.randomUUID().toString(), new UsersEntity(userId));
                session.save(entity);
                codes.add(new RegistrationCodes(entity));
            }
            return codes;
        });
    }
}