package com.diluv.api.v1.games;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;
import org.jboss.resteasy.plugins.providers.atom.Person;

import com.diluv.api.data.*;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.ImageUtil;
import com.diluv.api.utils.Pagination;
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.error.ErrorType;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.confluencia.database.record.CategoryRecord;
import com.diluv.confluencia.database.record.GameRecord;
import com.diluv.confluencia.database.record.GameVersionRecord;
import com.diluv.confluencia.database.record.ProjectAuthorRecord;
import com.diluv.confluencia.database.record.ProjectFileRecord;
import com.diluv.confluencia.database.record.ProjectLinkRecord;
import com.diluv.confluencia.database.record.ProjectRecord;
import com.diluv.confluencia.database.record.ProjectTypeRecord;
import com.diluv.confluencia.database.sort.GameSort;
import com.diluv.confluencia.database.sort.NewsSort;
import com.diluv.confluencia.database.sort.ProjectFileSort;
import com.diluv.confluencia.database.sort.ProjectSort;
import com.github.slugify.Slugify;

import static com.diluv.api.Main.DATABASE;

@GZIP
@Path("/games")
public class GamesAPI {

    private final Slugify slugify = new Slugify();

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGames (@QueryParam("sort") String sort) {

        final List<GameRecord> gameRecords = DATABASE.gameDAO.findAll(GameSort.fromString(sort, GameSort.NAME));
        final List<DataGame> games = gameRecords.stream().map(DataGame::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(games);
    }

    @Cache(maxAge = 7200, mustRevalidate = true)
    @GET
    @Path("/sort")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGames () {

        List<String> games = Arrays.stream(GameSort.values()).map(Enum::name).collect(Collectors.toList());
        List<String> news = Arrays.stream(NewsSort.values()).map(Enum::name).collect(Collectors.toList());
        List<String> projects = Arrays.stream(ProjectSort.values()).map(Enum::name).collect(Collectors.toList());
        List<String> projectFiles = Arrays.stream(ProjectFileSort.values()).map(Enum::name).collect(Collectors.toList());

        return ResponseUtil.successResponse(new DataSort(games, news, projects, projectFiles));
    }

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGame (@PathParam("gameSlug") String gameSlug) {

        final GameRecord gameRecord = DATABASE.gameDAO.findOneBySlug(gameSlug);
        if (gameRecord == null) {

            return ErrorMessage.NOT_FOUND_GAME.respond();
        }

        final List<GameVersionRecord> gameVersionRecords = DATABASE.gameDAO.findAllGameVersionsByGameSlug(gameSlug);
        List<DataGameVersion> versions = gameVersionRecords.stream().map(DataGameVersion::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(new DataGame(gameRecord, versions));
    }

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}/types")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectTypes (@PathParam("gameSlug") String gameSlug) {

        if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {

            return ErrorMessage.NOT_FOUND_GAME.respond();
        }

        final List<ProjectTypeRecord> projectTypesRecords = DATABASE.projectDAO.findAllProjectTypesByGameSlug(gameSlug);
        final List<DataProjectType> projectTypes = projectTypesRecords.stream().map(DataProjectType::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(projectTypes);
    }

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}/{projectTypeSlug}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectType (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug) {

        final ProjectTypeRecord projectTypesRecords = DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);

        if (projectTypesRecords == null) {

            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
        }

        final List<CategoryRecord> categoryRecords = DATABASE.projectDAO.findAllCategoriesByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);
        List<DataCategory> categories = categoryRecords.stream().map(DataCategory::new).collect(Collectors.toList());

        return ResponseUtil.successResponse(new DataProjectType(projectTypesRecords, categories));
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/projects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjects (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @QueryParam("page") Long queryPage, @QueryParam("limit") Integer queryLimit, @QueryParam("sort") String sort, @QueryParam("version") String version) {

        long page = Pagination.getPage(queryPage);
        int limit = Pagination.getLimit(queryLimit);
        final List<ProjectRecord> projectRecords;

        if (version == null) {
            projectRecords = DATABASE.projectDAO.findAllProjectsByGameSlugAndProjectType(gameSlug, projectTypeSlug, page, limit, ProjectSort.fromString(sort, ProjectSort.POPULARITY));
        }
        else {
            projectRecords = DATABASE.projectDAO.findAllProjectsByGameSlugAndProjectTypeAndVersion(gameSlug, projectTypeSlug, page, limit, ProjectSort.fromString(sort, ProjectSort.POPULARITY), version);
        }

        if (projectRecords.isEmpty()) {

            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {

                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }
        }

        final List<DataBaseProject> projects = projectRecords.stream().map(projectRecord -> {
            final List<CategoryRecord> categoryRecords = DATABASE.projectDAO.findAllCategoriesByProjectId(projectRecord.getId());
            List<DataCategory> categories = categoryRecords.stream().map(DataCategory::new).collect(Collectors.toList());
            return new DataBaseProject(projectRecord, categories);
        }).collect(Collectors.toList());

        return ResponseUtil.successResponse(projects);
    }

    @Cache(maxAge = 30, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/feed.atom")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response getProjectFeed (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug) {

        final List<ProjectRecord> projectRecords = DATABASE.projectDAO.findAllProjectsByGameSlugAndProjectType(gameSlug, projectTypeSlug, 1, 25, ProjectSort.NEW);

        if (projectRecords.isEmpty()) {
            return Response.status(ErrorType.BAD_REQUEST.getCode()).build();
        }

        final String baseUrl = String.format("%s/games/%s/%s", Constants.WEBSITE_URL, gameSlug, projectTypeSlug);
        Feed feed = new Feed();
        feed.setId(URI.create(baseUrl + "/feed.atom"));
        feed.getLinks().add(new Link("self", baseUrl));
        feed.setUpdated(new Date());
        feed.setTitle("Project Feed");
        for (ProjectRecord record : projectRecords) {
            Content content = new Content();
            content.setText(record.getSummary());
            Entry entry = new Entry();
            entry.setId(URI.create(baseUrl + "/" + record.getSlug()));
            entry.setTitle(record.getName());
            entry.setUpdated(new Date(record.getUpdatedAt()));
            entry.setContent(content);
            entry.getAuthors().add(new Person(record.getUserDisplayName()));
            feed.getEntries().add(entry);
        }

        return Response.ok(feed).build();
    }

    @Cache(maxAge = 30, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug) {

        final ProjectRecord projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null || !projectRecord.isReleased() && token == null) {
            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {

                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }

            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        final List<ProjectLinkRecord> projectLinkRecords = DATABASE.projectDAO.findAllLinksByProjectId(projectRecord.getId());
        final List<DataProjectLink> projectLinks = projectLinkRecords.stream().map(DataProjectLink::new).collect(Collectors.toList());

        final List<ProjectAuthorRecord> records = DATABASE.projectDAO.findAllProjectAuthorsByProjectId(projectRecord.getId());

        final List<CategoryRecord> categoryRecords = DATABASE.projectDAO.findAllCategoriesByProjectId(projectRecord.getId());
        List<DataCategory> categories = categoryRecords.stream().map(DataCategory::new).collect(Collectors.toList());

        if (token != null) {
            List<String> permissions = ProjectPermissions.getAuthorizedUserPermissions(projectRecord, token, records);

            if (permissions != null) {
                final List<DataProjectContributor> projectAuthors = records.stream().map(DataProjectContributorAuthorized::new).collect(Collectors.toList());
                return ResponseUtil.successResponse(new DataProjectAuthorized(projectRecord, categories, projectAuthors, projectLinks, permissions));
            }
        }

        final List<DataProjectContributor> projectAuthors = records.stream().map(DataProjectContributor::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(new DataProject(projectRecord, categories, projectAuthors, projectLinks));
    }

    @Cache(maxAge = 30, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}/feed.atom")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response getProjectFileFeed (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug) {

        final ProjectRecord projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null) {
            return Response.status(ErrorType.BAD_REQUEST.getCode()).build();
        }

        final List<ProjectFileRecord> projectFileRecords = DATABASE.fileDAO.findAllByProjectId(projectRecord.getId(), false, 1, 25, ProjectFileSort.NEW);

        final String baseUrl = String.format("%s/games/%s/%s/%s", Constants.WEBSITE_URL, gameSlug, projectTypeSlug, projectSlug);
        Feed feed = new Feed();
        feed.setId(URI.create(baseUrl + "/feed.atom"));
        feed.getLinks().add(new Link("self", baseUrl + "/"));
        feed.setUpdated(new Date());
        feed.setTitle(projectRecord.getName() + " File Feed");
        for (ProjectFileRecord record : projectFileRecords) {
            Content content = new Content();
            content.setText(record.getChangelog());
            Entry entry = new Entry();
            entry.setId(URI.create(baseUrl + "/files/" + record.getId()));
            entry.setTitle(record.getName());
            entry.setUpdated(new Date(record.getUpdatedAt()));
            entry.setContent(content);
            entry.getAuthors().add(new Person(record.getUserDisplayName()));
            feed.getEntries().add(entry);
        }

        return Response.ok(feed).build();
    }

    @Cache(maxAge = 60, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}/files")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectFiles (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug, @QueryParam("page") Long queryPage, @QueryParam("limit") Integer queryLimit, @QueryParam("sort") String sort, @QueryParam("version") String version) {

        long page = Pagination.getPage(queryPage);
        int limit = Pagination.getLimit(queryLimit);

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

        boolean authorized = token != null && ProjectPermissions.hasPermission(projectRecord, token, ProjectPermissions.FILE_UPLOAD);
        final List<ProjectFileRecord> projectFileRecords;

        if (version == null) {
            projectFileRecords = DATABASE.fileDAO.findAllByProjectId(projectRecord.getId(), authorized, page, limit, ProjectFileSort.fromString(sort, ProjectFileSort.NEW));
        }
        else {
            projectFileRecords = DATABASE.fileDAO.findAllByProjectIdWhereVersion(projectRecord.getId(), authorized, page, limit, ProjectFileSort.fromString(sort, ProjectFileSort.NEW), version);
        }

        final List<DataProjectFile> projectFiles = projectFileRecords.stream().map(record -> {
            final List<GameVersionRecord> gameVersionRecords = DATABASE.fileDAO.findAllGameVersionsById(projectRecord.getId());
            List<DataGameVersion> gameVersions = gameVersionRecords.stream().map(DataGameVersion::new).collect(Collectors.toList());
            final List<Long> dependencies = DATABASE.fileDAO.findAllProjectDependenciesById(record.getId());
            return record.isReleased() ?
                new DataProjectFileAvailable(record, dependencies, gameVersions, gameSlug, projectTypeSlug, projectSlug) :
                new DataProjectFileInQueue(record, dependencies, gameVersions, gameSlug, projectTypeSlug, projectSlug);
        }).collect(Collectors.toList());
        return ResponseUtil.successResponse(projectFiles);
    }

    @POST
    @Path("/{gameSlug}/{projectTypeSlug}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postProject (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @MultipartForm ProjectCreateForm form) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {
            return ErrorMessage.NOT_FOUND_GAME.respond();
        }

        if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
            return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
        }

        if (!Validator.validateProjectName(form.name)) {
            return ErrorMessage.PROJECT_INVALID_NAME.respond();
        }

        if (!Validator.validateProjectSummary(form.summary)) {
            return ErrorMessage.PROJECT_INVALID_SUMMARY.respond();
        }

        if (!Validator.validateProjectDescription(form.description)) {
            return ErrorMessage.PROJECT_INVALID_DESCRIPTION.respond();
        }

        if (form.logo == null) {
            return ErrorMessage.PROJECT_INVALID_LOGO.respond();
        }

        final BufferedImage image = ImageUtil.isValidImage(form.logo, 1000000L);

        if (image == null) {
            return ErrorMessage.PROJECT_INVALID_LOGO.respond();
        }

        String name = form.name.trim();
        String summary = form.summary.trim();
        String description = form.description.trim();

        final String projectSlug = this.slugify.slugify(name);

        if (DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug) != null) {
            return ErrorMessage.PROJECT_TAKEN_SLUG.respond();
        }
        if (!DATABASE.projectDAO.insertProject(projectSlug, name, summary, description, token.getUserId(), gameSlug, projectTypeSlug)) {
            return ErrorMessage.FAILED_CREATE_PROJECT.respond();
        }

        final ProjectRecord projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);

        if (projectRecord == null) {
            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        final File file = new File(Constants.CDN_FOLDER, String.format("games/%s/%s/%d/logo.png", gameSlug, projectTypeSlug, projectRecord.getId()));
        if (!ImageUtil.saveImage(image, file)) {
            return ErrorMessage.ERROR_SAVING_IMAGE.respond();
        }

        final List<CategoryRecord> categoryRecords = DATABASE.projectDAO.findAllCategoriesByProjectId(projectRecord.getId());
        List<DataCategory> categories = categoryRecords.stream().map(DataCategory::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(new DataProject(projectRecord, categories));
    }
}
