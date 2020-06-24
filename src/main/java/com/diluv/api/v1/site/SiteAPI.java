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

import com.diluv.api.Database;
import com.diluv.api.data.site.DataSiteProjectFileDisplay;
import com.diluv.api.data.site.DataSiteProjectFilesPage;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.query.ProjectFileQuery;
import com.diluv.confluencia.database.record.GameVersionRecord;
import com.diluv.confluencia.database.record.ProjectFileRecord;
import com.diluv.confluencia.database.record.UserRecord;
import com.diluv.confluencia.database.sort.ProjectFileSort;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.Query;
import org.jboss.resteasy.annotations.cache.Cache;

import com.diluv.api.data.*;
import com.diluv.api.data.site.DataSiteGame;
import com.diluv.api.data.site.DataSiteGameProjects;
import com.diluv.api.data.site.DataSiteIndex;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.api.utils.query.GameQuery;
import com.diluv.api.utils.query.ProjectQuery;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.api.v1.games.GamesAPI;
import com.diluv.confluencia.database.record.GameRecord;
import com.diluv.confluencia.database.record.ProjectAuthorRecord;
import com.diluv.confluencia.database.record.ProjectLinkRecord;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.diluv.confluencia.database.record.ProjectTypeRecord;
import com.diluv.confluencia.database.record.TagRecord;
import com.diluv.confluencia.database.sort.GameSort;
import com.diluv.confluencia.database.sort.ProjectSort;
import com.diluv.confluencia.database.sort.Sort;

import static com.diluv.api.Main.DATABASE;

@GZIP
@Path("/site")
@Produces(MediaType.APPLICATION_JSON)
public class SiteAPI {

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/")
    public Response getIndex () {

        final List<GameRecord> gameRecords = DATABASE.gameDAO.findFeaturedGames();
        final List<DataSiteGame> games = gameRecords.stream().map(DataSiteGame::new).collect(Collectors.toList());

        final long projectCount = DATABASE.projectDAO.countAll();
        final long userCount = DATABASE.userDAO.countAll();
        final long gameCount = DATABASE.gameDAO.countAll("");
        final long projectTypeCount = 0;
        return ResponseUtil.successResponse(new DataSiteIndex(games, projectCount, userCount, gameCount, projectTypeCount));
    }

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/games")
    public Response getGames (@Query GameQuery query) {

        final long page = query.getPage();
        final int limit = query.getLimit();
        final Sort sort = query.getSort(GameSort.NAME);
        final String search = query.getSearch();

        final List<GameRecord> gameRecords = DATABASE.gameDAO.findAll(page, limit, sort, search);

        final long gameCount = DATABASE.gameDAO.countAll(search);
        final List<DataBaseGame> games = gameRecords.stream().map(DataSiteGame::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(new DataGameList(games, GamesAPI.GAME_SORTS, gameCount));
    }

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/games/{gameSlug}")
    public Response getGameDefaultType (@PathParam("gameSlug") String gameSlug) {

        final GameRecord gameRecord = DATABASE.gameDAO.findOneBySlug(gameSlug);
        if (gameRecord == null) {

            return ErrorMessage.NOT_FOUND_GAME.respond();
        }

        return ResponseUtil.successResponse(gameRecord.getDefaultProjectType());
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

        final List<ProjectRecord> projectRecords = DATABASE.projectDAO.findAllByGameAndProjectType(gameSlug, projectTypeSlug, search, page, limit, sort, versions, tags);

        GameRecord game = DATABASE.gameDAO.findOneBySlug(gameSlug);
        if (projectRecords.isEmpty()) {

            if (game == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {

                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }
        }
        final List<DataBaseProject> projects = projectRecords.stream().map(projectRecord -> {
            final List<TagRecord> tagRecords = DATABASE.projectDAO.findAllTagsByProjectId(projectRecord.getId());
            final List<DataTag> dataTags = tagRecords.stream().map(DataTag::new).collect(Collectors.toList());
            return new DataBaseProject(projectRecord, dataTags);
        }).collect(Collectors.toList());

        final List<DataBaseProjectType> types = DATABASE.projectDAO.findAllProjectTypesByGameSlug(gameSlug).stream().map(DataBaseProjectType::new).collect(Collectors.toList());
        final ProjectTypeRecord currentType = DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug, search);
        final List<DataTag> dataTags = DATABASE.projectDAO.findAllTagsByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug).stream().map(DataTag::new).collect(Collectors.toList());

        return ResponseUtil.successResponse(new DataSiteGameProjects(projects, types, new DataProjectType(currentType, dataTags), GamesAPI.PROJECT_SORTS));
    }


    @Cache(maxAge = 30, mustRevalidate = true)
    @GET
    @Path("/projects/{gameSlug}/{projectTypeSlug}/{projectSlug}")
    public Response getProject (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug) {

        final ProjectRecord projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null || !projectRecord.isReleased() && token == null) {
            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        final List<ProjectLinkRecord> projectLinkRecords = DATABASE.projectDAO.findAllLinksByProjectId(projectRecord.getId());
        final List<DataProjectLink> projectLinks = projectLinkRecords.stream().map(DataProjectLink::new).collect(Collectors.toList());

        final List<ProjectAuthorRecord> records = DATABASE.projectDAO.findAllProjectAuthorsByProjectId(projectRecord.getId());

        final List<TagRecord> tagRecords = DATABASE.projectDAO.findAllTagsByProjectId(projectRecord.getId());
        List<DataTag> tags = tagRecords.stream().map(DataTag::new).collect(Collectors.toList());

        if (token != null) {
            List<String> permissions = ProjectPermissions.getAuthorizedUserPermissions(projectRecord, token, records);

            if (permissions != null) {
                final List<DataProjectContributor> projectAuthors = records.stream().map(DataProjectContributorAuthorized::new).collect(Collectors.toList());
                return ResponseUtil.successResponse(new DataProjectAuthorized(projectRecord, tags, projectAuthors, projectLinks, permissions));
            }
        }

        final List<DataProjectContributor> projectAuthors = records.stream().map(DataProjectContributor::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(new DataProject(projectRecord, tags, projectAuthors, projectLinks));
    }

    @GET
    @Path("/games/{gameSlug}/{projectTypeSlug}/{projectSlug}/files")
    public Response getProjectFiles (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug, @Query ProjectFileQuery query) {

        long page = query.getPage();
        int limit = query.getLimit();
        Sort sort = query.getSort(ProjectFileSort.NEW);
        String version = query.getVersions();

        final ProjectRecord projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null) {

            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {
                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }

            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        final List<ProjectAuthorRecord> records = DATABASE.projectDAO.findAllProjectAuthorsByProjectId(projectRecord.getId());

        boolean authorized = token != null && ProjectPermissions.hasPermission(projectRecord, token, ProjectPermissions.FILE_UPLOAD);
        final List<ProjectFileRecord> projectFileRecords;

        if (version == null) {
            projectFileRecords = DATABASE.fileDAO.findAllByProjectId(projectRecord.getId(), authorized, page, limit, sort);
        }
        else {
            projectFileRecords = DATABASE.fileDAO.findAllByProjectIdWhereVersion(projectRecord.getId(), authorized, page, limit, sort, version);
        }

        final List<DataSiteProjectFileDisplay> projectFiles = projectFileRecords.stream().map(record -> {
            final List<GameVersionRecord> gameVersionRecords = DATABASE.fileDAO.findAllGameVersionsById(projectRecord.getId());
            List<DataGameVersion> gameVersions = gameVersionRecords.stream().map(DataGameVersion::new).collect(Collectors.toList());
            return record.isReleased() ?
                new DataSiteProjectFileDisplay(record, gameVersions, gameSlug, projectTypeSlug, projectSlug) :
                new DataSiteProjectFileDisplay(record, gameVersions, gameSlug, projectTypeSlug, projectSlug);
        }).collect(Collectors.toList());
        List<DataTag> tags = DATABASE.projectDAO.findAllTagsByProjectId(projectRecord.getId()).stream().map(DataTag::new).collect(Collectors.toList());
        final List<DataProjectContributor> projectAuthors = records.stream().map(DataProjectContributor::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(new DataSiteProjectFilesPage(new DataBaseProject(projectRecord, tags, projectAuthors), projectFiles));
    }

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/author/{username}")
    public Response getUser (@PathParam("username") String username, @HeaderParam("Authorization") String auth) {

        final UserRecord userRecord = DATABASE.userDAO.findOneByUsername(username);

        if (userRecord == null) {

            return ErrorMessage.NOT_FOUND_USER.respond();
        }

        if (auth != null) {

            final Token token = JWTUtil.getToken(auth);

            if (token != null) {
                final UserRecord tokenUser = DATABASE.userDAO.findOneByUserId(token.getUserId());

                if (tokenUser.getUsername().equalsIgnoreCase(username)) {
                    return ResponseUtil.successResponse(new DataAuthorizedUser(userRecord));
                }
            }
        }

        return ResponseUtil.successResponse(new DataUser(userRecord));
    }
}
