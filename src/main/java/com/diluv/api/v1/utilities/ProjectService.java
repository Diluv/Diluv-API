package com.diluv.api.v1.utilities;

import java.util.List;

import com.diluv.api.data.DataAuthorizedProject;
import com.diluv.api.data.DataProject;
import com.diluv.api.provider.ResponseException;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.confluencia.database.record.ProjectsEntity;

import static com.diluv.api.Main.DATABASE;

public class ProjectService {

    public static DataProject getDataProject (String gameSlug, String projectTypeSlug, String projectSlug, Token token) throws ResponseException {

        final ProjectsEntity project = DATABASE.project.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (project == null) {
            if (DATABASE.game.findOneBySlug(gameSlug) == null) {
                throw new ResponseException(ErrorMessage.NOT_FOUND_GAME.respond());
            }

            if (DATABASE.project.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
                throw new ResponseException(ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond());
            }

            throw new ResponseException(ErrorMessage.NOT_FOUND_PROJECT.respond());
        }

        return getDataProject(project, token);
    }

    public static DataProject getDataProject (long projectId, Token token) throws ResponseException {

        final ProjectsEntity project = DATABASE.project.findOneProjectByProjectId(projectId);

        if (project == null) {
            throw new ResponseException(ErrorMessage.NOT_FOUND_PROJECT.respond());
        }
        return getDataProject(project, token);
    }

    public static DataProject getDataProject (ProjectsEntity project, Token token) throws ResponseException {

        if (!project.isReleased()) {

            if (token == null)
                throw new ResponseException(ErrorMessage.NOT_FOUND_PROJECT.respond());

            List<String> permissions = ProjectPermissions.getAuthorizedUserPermissions(project, token);

            if (permissions != null) {
                return new DataAuthorizedProject(project, permissions);
            }

            throw new ResponseException(ErrorMessage.NOT_FOUND_PROJECT.respond());
        }

        return new DataProject(project);
    }
}
