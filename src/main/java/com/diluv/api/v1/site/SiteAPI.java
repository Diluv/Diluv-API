package com.diluv.api.v1.site;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.Query;

import com.diluv.api.data.DataAuthorizedProject;
import com.diluv.api.data.DataAuthorizedUser;
import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataGameVersion;
import com.diluv.api.data.DataProject;
import com.diluv.api.data.DataProjectType;
import com.diluv.api.data.DataSlugName;
import com.diluv.api.data.DataUser;
import com.diluv.api.data.site.*;
import com.diluv.api.provider.ResponseException;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.api.utils.query.AuthorProjectsQuery;
import com.diluv.api.utils.query.ProjectFileQuery;
import com.diluv.api.utils.query.ProjectQuery;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.api.v1.utilities.ProjectService;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.FeaturedGamesEntity;
import com.diluv.confluencia.database.record.GameVersionsEntity;
import com.diluv.confluencia.database.record.GamesEntity;
import com.diluv.confluencia.database.record.ProjectFileGameVersionsEntity;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.diluv.confluencia.database.record.ProjectTypesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.TagsEntity;
import com.diluv.confluencia.database.record.UsersEntity;
import com.diluv.confluencia.database.sort.GameSort;
import com.diluv.confluencia.database.sort.ProjectFileSort;
import com.diluv.confluencia.database.sort.ProjectSort;

@ApplicationScoped
@GZIP
@Path("/site")
@Produces(MediaType.APPLICATION_JSON)
public class SiteAPI {

    public static final List<DataSlugName> GAME_SORTS = GameSort.LIST.stream().map(a -> new DataSlugName(a.getSlug(), a.getDisplayName())).collect(Collectors.toList());
    public static final List<DataSlugName> PROJECT_SORTS = ProjectSort.LIST.stream().map(a -> new DataSlugName(a.getSlug(), a.getDisplayName())).collect(Collectors.toList());
    public static final List<DataSlugName> PROJECT_FILE_SORTS = ProjectFileSort.LIST.stream().map(a -> new DataSlugName(a.getSlug(), a.getDisplayName())).collect(Collectors.toList());

    @GET
    @Path("/")
    public Response getIndex () {

        return Confluencia.getTransaction(session -> {
            final List<FeaturedGamesEntity> gameRecords = Confluencia.GAME.findFeaturedGames(session);
            final List<DataSiteGame> games = gameRecords.stream().map(DataSiteGame::new).collect(Collectors.toList());

            final long projectCount = Confluencia.PROJECT.countAllByGameSlug(session, "");
            final long userCount = Confluencia.USER.countAll(session);
            final long gameCount = Confluencia.GAME.countAllBySearch(session, "");
            final long projectTypeCount = Confluencia.GAME.countAllProjectTypes(session);
            return ResponseUtil.successResponse(new DataSiteIndex(games, projectCount, userCount, gameCount, projectTypeCount));
        });
    }

    @GET
    @Path("/sort")
    public Response getSorts () {

        return ResponseUtil.successResponse(new DataSiteSorts(GAME_SORTS, PROJECT_SORTS, PROJECT_FILE_SORTS));
    }

    @GET
    @Path("/games/{gameSlug}")
    public Response getGameDefaultType (@PathParam("gameSlug") String gameSlug) {

        return Confluencia.getTransaction(session -> {
            final GamesEntity gameRecord = Confluencia.GAME.findOneBySlug(session, gameSlug);
            if (gameRecord == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            return ResponseUtil.successResponse(gameRecord.getDefaultProjectTypeSlug());
        });
    }

    @GET
    @Path("/games/{gameSlug}/types")
    public Response getGameTypes (@PathParam("gameSlug") String gameSlug, @Query ProjectQuery query) {

        return Confluencia.getTransaction(session -> {

            GamesEntity game = Confluencia.GAME.findOneBySlug(session, gameSlug);

            if (game == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            final List<DataSlugName> types = game.getProjectTypes().stream().map(a -> new DataSlugName(a.getSlug(), a.getName())).collect(Collectors.toList());

            return ResponseUtil.successResponse(new DataSiteGameProjectTypes(types));
        });
    }

    @GET
    @Path("/games/{gameSlug}/{projectTypeSlug}")
    public Response getProjects (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @Query ProjectQuery query) {

        final String search = query.getSearch();
        final String versions = query.getVersions();
        final Set<String> tags = query.getTags();
        final Set<String> loaders = query.getLoaders();

        return Confluencia.getTransaction(session -> {
            final ProjectTypesEntity currentType = Confluencia.PROJECT.findOneProjectTypeByGameSlugAndProjectTypeSlug(session, gameSlug, projectTypeSlug);

            if (currentType == null) {

                if (Confluencia.GAME.findOneBySlug(session, gameSlug) == null) {

                    return ErrorMessage.NOT_FOUND_GAME.respond();
                }

                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }

            final long projectCount = Confluencia.PROJECT.countAllByGameAndProjectType(session, gameSlug, projectTypeSlug, search, versions, tags, loaders);

            return ResponseUtil.successResponse(new DataSiteGameProjects(new DataProjectType(currentType, projectCount)));
        });
    }

    @GET
    @Path("/projects/{gameSlug}/{projectTypeSlug}/{projectSlug}/settings")
    public Response getProjectSettings (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug) throws ResponseException {

        return Confluencia.getTransaction(session -> {
            try {
                final DataBaseProject project = ProjectService.getDataProject(session, gameSlug, projectTypeSlug, projectSlug, token);
                return ResponseUtil.successResponse(new DataSiteProjectSettings(project, Confluencia.PROJECT.findAllTagsByGameSlugAndProjectTypeSlug(session, gameSlug, projectTypeSlug).stream().map(a -> new DataSlugName(a.getSlug(), a.getName())).collect(Collectors.toList())));
            }
            catch (ResponseException e) {
                e.printStackTrace();
                return e.getResponse();
            }
        });
    }

    @GET
    @Path("/games/{gameSlug}/{projectTypeSlug}/{projectSlug}/files/{fileId}")
    public Response getProjectFile (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug, @PathParam("fileId") long fileId, @Query ProjectFileQuery query) throws ResponseException {

        return Confluencia.getTransaction(session -> {
            try {
                final ProjectsEntity project = ProjectService.getProject(session, gameSlug, projectTypeSlug, projectSlug, token);


                final ProjectFilesEntity projectFile = Confluencia.FILE.findOneById(session, fileId);

                if (projectFile == null) {
                    return ErrorMessage.NOT_FOUND_PROJECT_FILE.respond();
                }

                boolean authorized = ProjectPermissions.hasPermission(project, token, ProjectPermissions.FILE_UPLOAD);

                if (!authorized && !projectFile.isReleased()) {
                    return ErrorMessage.NOT_FOUND_PROJECT_FILE.respond();
                }

                final List<GameVersionsEntity> gameVersionRecords = projectFile.getGameVersions().stream().map(ProjectFileGameVersionsEntity::getGameVersion).collect(Collectors.toList());
                final List<DataGameVersion> gameVersions = gameVersionRecords.stream().map(DataGameVersion::new).collect(Collectors.toList());

                final DataBaseProject dataProject = ProjectService.getBaseDataProject(project, token);
                return ResponseUtil.successResponse(new DataSiteProjectFilePage(dataProject, new DataSiteProjectFileDisplay(projectFile, gameVersions)));
            }
            catch (ResponseException e) {
                return e.getResponse();
            }
        });
    }

    @GET
    @Path("/author/{username}")
    public Response getUser (@HeaderParam("Authorization") Token token, @PathParam("username") String username, @Query AuthorProjectsQuery query) {

        return Confluencia.getTransaction(session -> {
            final UsersEntity userRecord = Confluencia.USER.findOneByUsername(session, username);
            if (userRecord == null) {

                return ErrorMessage.NOT_FOUND_USER.respond();
            }

            boolean authorized = token != null && token.getUserId() == userRecord.getId();
            long projectCount = Confluencia.PROJECT.countAllByUserId(session, userRecord.getId(), authorized);

            List<ProjectsEntity> projects = Confluencia.PROJECT.findAllByUserId(session, userRecord.getId(), authorized, query.getPage(), query.getLimit(), query.getSort(ProjectFileSort.NEW));

            if (authorized) {
                List<DataProject> dataProjects = projects.stream().map(a -> new DataAuthorizedProject(a, ProjectPermissions.getAuthorizedUserPermissions(a, token))).collect(Collectors.toList());
                DataUser user = new DataAuthorizedUser(userRecord);
                return ResponseUtil.successResponse(new DataSiteAuthorProjects(user, dataProjects, projectCount));
            }

            List<DataProject> dataProjects = projects.stream().map(DataProject::new).collect(Collectors.toList());
            DataUser user = new DataUser(userRecord);
            return ResponseUtil.successResponse(new DataSiteAuthorProjects(user, dataProjects, projectCount));
        });
    }

    @GET
    @Path("/create/games/{gameSlug}/{projectTypeSlug}")
    public Response createProject (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug) {

        return Confluencia.getTransaction(session -> {
            List<TagsEntity> tags = Confluencia.PROJECT.findAllTagsByGameSlugAndProjectTypeSlug(session, gameSlug, projectTypeSlug);
            return ResponseUtil.successResponse(new DataCreateProject(tags.stream().map(a -> new DataSlugName(a.getSlug(), a.getName())).collect(Collectors.toList())));
        });
    }

    @GET
    @Path("/search/users/{search}")
    public Response searchUsers (@PathParam("search") String search) {

        return Confluencia.getTransaction(session -> {
            List<UsersEntity> users = Confluencia.USER.findLimitBySearch(session, search);
            return ResponseUtil.successResponse(users.stream().map(DataUser::new).collect(Collectors.toList()));
        });
    }
}
