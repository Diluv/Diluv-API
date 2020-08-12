package com.diluv.api.v1.site;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.Query;

import com.diluv.api.data.*;
import com.diluv.api.data.site.DataSiteAuthorProjects;
import com.diluv.api.data.site.DataSiteGame;
import com.diluv.api.data.site.DataSiteGameProjects;
import com.diluv.api.data.site.DataSiteIndex;
import com.diluv.api.data.site.DataSiteProjectFileDisplay;
import com.diluv.api.data.site.DataSiteProjectFilesPage;
import com.diluv.api.provider.ResponseException;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.api.utils.query.AuthorProjectsQuery;
import com.diluv.api.utils.query.GameQuery;
import com.diluv.api.utils.query.ProjectFileQuery;
import com.diluv.api.utils.query.ProjectQuery;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.api.v1.games.GamesAPI;
import com.diluv.api.v1.utilities.ProjectService;
import com.diluv.confluencia.database.record.FeaturedGamesEntity;
import com.diluv.confluencia.database.record.GameVersionsEntity;
import com.diluv.confluencia.database.record.GamesEntity;
import com.diluv.confluencia.database.record.ProjectFileGameVersionsEntity;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.diluv.confluencia.database.record.ProjectTypesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.UsersEntity;
import com.diluv.confluencia.database.sort.GameSort;
import com.diluv.confluencia.database.sort.ProjectFileSort;
import com.diluv.confluencia.database.sort.ProjectSort;
import com.diluv.confluencia.database.sort.Sort;

import static com.diluv.api.Main.DATABASE;

@GZIP
@Path("/site")
@Produces(MediaType.APPLICATION_JSON)
public class SiteAPI {

    @GET
    @Path("/")
    public Response getIndex () {

        final List<FeaturedGamesEntity> gameRecords = DATABASE.gameDAO.findFeaturedGames();
        final List<DataSiteGame> games = gameRecords.stream().map(DataSiteGame::new).collect(Collectors.toList());

        final long projectCount = DATABASE.projectDAO.countAllByGameSlug("");
        final long userCount = DATABASE.userDAO.countAll();
        final long gameCount = DATABASE.gameDAO.countAllBySearch("");
        final long projectTypeCount = DATABASE.gameDAO.countAllProjectTypes();
        return ResponseUtil.successResponse(new DataSiteIndex(games, projectCount, userCount, gameCount, projectTypeCount));
    }

    @GET
    @Path("/games")
    public Response getGames (@Query GameQuery query) {

        final long page = query.getPage();
        final int limit = query.getLimit();
        final Sort sort = query.getSort(GameSort.NAME);
        final String search = query.getSearch();

        final List<GamesEntity> gameRecords = DATABASE.gameDAO.findAll(page, limit, sort, search);

        final long gameCount = DATABASE.gameDAO.countAllBySearch(search);
        final List<DataBaseGame> games = gameRecords.stream().map(DataSiteGame::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(new DataGameList(games, GamesAPI.GAME_SORTS, gameCount));
    }

    @GET
    @Path("/games/{gameSlug}")
    public Response getGameDefaultType (@PathParam("gameSlug") String gameSlug) {

        final GamesEntity gameRecord = DATABASE.gameDAO.findOneBySlug(gameSlug);
        if (gameRecord == null) {

            return ErrorMessage.NOT_FOUND_GAME.respond();
        }

        return ResponseUtil.successResponse(gameRecord.getDefaultProjectTypeEntity().getSlug());
    }


    @GET
    @Path("/games/{gameSlug}/{projectTypeSlug}/projects")
    public Response getProjects (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @Query ProjectQuery query) {

        final long page = query.getPage();
        final int limit = query.getLimit();
        final Sort sort = query.getSort(ProjectSort.POPULAR);
        final String search = query.getSearch();
        final String versions = query.getVersions();
        final String[] tags = query.getTags();

        final List<ProjectsEntity> projects = DATABASE.projectDAO.findAllByGameAndProjectType(gameSlug, projectTypeSlug, search, page, limit, sort, versions, tags);

        GamesEntity game = DATABASE.gameDAO.findOneBySlug(gameSlug);
        if (projects.isEmpty()) {

            if (game == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {

                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }
        }
        final List<DataBaseProject> dataProjects = projects.stream().map(DataBaseProject::new).collect(Collectors.toList());

        final List<DataBaseProjectType> types = game.getProjectTypes().stream().map(DataBaseProjectType::new).collect(Collectors.toList());
        final ProjectTypesEntity currentType = DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);

        final long projectCount = DATABASE.projectDAO.countAllByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);

        return ResponseUtil.successResponse(new DataSiteGameProjects(dataProjects, types, new DataProjectType(currentType, projectCount), GamesAPI.PROJECT_SORTS));
    }


    @GET
    @Path("/projects/{gameSlug}/{projectTypeSlug}/{projectSlug}")
    public Response getProject (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug) throws ResponseException {

        DataProject project = ProjectService.getDataProject(gameSlug, projectTypeSlug, projectSlug, token);

        return ResponseUtil.successResponse(project);
    }

    @GET
    @Path("/games/{gameSlug}/{projectTypeSlug}/{projectSlug}/files")
    public Response getProjectFiles (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug, @Query ProjectFileQuery query) throws ResponseException {

        long page = query.getPage();
        int limit = query.getLimit();
        Sort sort = query.getSort(ProjectFileSort.NEW);
        String gameVersion = query.getGameVersion();

        final ProjectsEntity project = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (project == null) {

            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {
                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }

            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        boolean authorized = token != null && ProjectPermissions.hasPermission(project, token, ProjectPermissions.FILE_UPLOAD);
        final List<ProjectFilesEntity> projectFileRecords = DATABASE.fileDAO.findAllByProject(project, authorized, page, limit, sort, gameVersion);

        final List<DataSiteProjectFileDisplay> projectFiles = projectFileRecords.stream().map(record -> {
            final List<GameVersionsEntity> gameVersionRecords = record.getGameVersions().stream().map(ProjectFileGameVersionsEntity::getGameVersion).collect(Collectors.toList());
            List<DataGameVersion> gameVersions = gameVersionRecords.stream().map(DataGameVersion::new).collect(Collectors.toList());
            return record.isReleased() ?
                new DataSiteProjectFileDisplay(record, gameVersions, gameSlug, projectTypeSlug, projectSlug) :
                new DataSiteProjectFileDisplay(record, gameVersions, gameSlug, projectTypeSlug, projectSlug);
        }).collect(Collectors.toList());
        return ResponseUtil.successResponse(new DataSiteProjectFilesPage(new DataBaseProject(project), projectFiles));
    }

    @GET
    @Path("/author/{username}")
    public Response getUser (@HeaderParam("Authorization") Token token, @PathParam("username") String username, @Query AuthorProjectsQuery query) {

        final UsersEntity userRecord = DATABASE.userDAO.findOneByUsername(username);
        if (userRecord == null) {

            return ErrorMessage.NOT_FOUND_USER.respond();
        }

        boolean authorized = token != null && token.getUserId() == userRecord.getId();
        long projectCount = DATABASE.projectDAO.countAllByUsername(username, authorized);

        List<ProjectsEntity> projects = DATABASE.projectDAO.findAllByUsername(username, authorized, query.getPage(), query.getLimit(), query.getSort(ProjectFileSort.NEW));

        if (authorized) {
            List<DataProject> dataProjects = projects.stream().map(a -> new DataAuthorizedProject(a, ProjectPermissions.getAuthorizedUserPermissions(a, token))).collect(Collectors.toList());
            DataUser user = new DataAuthorizedUser(userRecord);
            return ResponseUtil.successResponse(new DataSiteAuthorProjects(user, dataProjects, GamesAPI.GAME_SORTS, projectCount));
        }

        List<DataProject> dataProjects = projects.stream().map(DataProject::new).collect(Collectors.toList());
        DataUser user = new DataUser(userRecord);
        return ResponseUtil.successResponse(new DataSiteAuthorProjects(user, dataProjects, GamesAPI.GAME_SORTS, projectCount));
    }
}
