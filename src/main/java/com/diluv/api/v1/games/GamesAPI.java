package com.diluv.api.v1.games;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.validator.GenericValidator;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.Query;
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
import com.diluv.api.utils.auth.Validator;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.error.ErrorType;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.api.utils.query.GameQuery;
import com.diluv.api.utils.query.ProjectFileQuery;
import com.diluv.api.utils.query.ProjectQuery;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.confluencia.database.record.GamesEntity;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.diluv.confluencia.database.record.ProjectTagsEntity;
import com.diluv.confluencia.database.record.ProjectTypesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.TagsEntity;
import com.diluv.confluencia.database.record.UsersEntity;
import com.diluv.confluencia.database.sort.GameSort;
import com.diluv.confluencia.database.sort.ProjectFileSort;
import com.diluv.confluencia.database.sort.ProjectSort;
import com.diluv.confluencia.database.sort.Sort;
import com.github.slugify.Slugify;

import static com.diluv.api.Main.DATABASE;

@GZIP
@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
public class GamesAPI {

    public static final List<DataSort> GAME_SORTS = GameSort.LIST.stream().map(DataSort::new).collect(Collectors.toList());
    public static final List<DataSort> PROJECT_SORTS = ProjectSort.LIST.stream().map(DataSort::new).collect(Collectors.toList());

    private final Slugify slugify = new Slugify();

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/")
    public Response getGames (@Query GameQuery query) {

        final long page = query.getPage();
        final int limit = query.getLimit();
        final Sort sort = query.getSort(GameSort.NAME);
        final String search = query.getSearch();

        final List<GamesEntity> gameRecords = DATABASE.gameDAO.findAll(page, limit, sort, search);

        final long gameCount = DATABASE.gameDAO.countAllBySearch(search);

        final List<DataBaseGame> games = gameRecords.stream().map(DataGame::new).collect(Collectors.toList());
        return ResponseUtil.successResponse(new DataGameList(games, GAME_SORTS, gameCount));
    }

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}")
    public Response getGame (@PathParam("gameSlug") String gameSlug) {

        final GamesEntity gameRecord = DATABASE.gameDAO.findOneBySlug(gameSlug);
        if (gameRecord == null) {

            return ErrorMessage.NOT_FOUND_GAME.respond();
        }

        final long projectCount = DATABASE.gameDAO.countAllProjectsBySlug(gameSlug);

        return ResponseUtil.successResponse(new DataGame(gameRecord, PROJECT_SORTS, projectCount));
    }

    @Cache(maxAge = 300, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}/{projectTypeSlug}")
    public Response getProjectType (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug) {

        final ProjectTypesEntity projectTypesRecords = DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);

        if (projectTypesRecords == null) {

            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
        }

        final long projectCount = DATABASE.projectDAO.countAllByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);
        return ResponseUtil.successResponse(new DataProjectType(projectTypesRecords, projectCount));
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/projects")
    public Response getProjects (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @Query ProjectQuery query) {

        final long page = query.getPage();
        final int limit = query.getLimit();
        final Sort sort = query.getSort(ProjectSort.POPULAR);
        final String search = query.getSearch();
        final String versions = query.getVersions();
        final String[] tags = query.getTags();

        final List<ProjectsEntity> projectRecords = DATABASE.projectDAO.findAllByGameAndProjectType(gameSlug, projectTypeSlug, search, page, limit, sort, versions, tags);

        if (projectRecords.isEmpty()) {

            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {

                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }
        }

        final List<DataBaseProject> projects = projectRecords.stream().map(DataBaseProject::new).collect(Collectors.toList());

        return ResponseUtil.successResponse(projects);
    }

    @Cache(maxAge = 30, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/feed.atom")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response getProjectFeed (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug) {

        final List<ProjectsEntity> projectRecords = DATABASE.projectDAO.findAllByGameAndProjectType(gameSlug, projectTypeSlug, "", 1, 25, ProjectSort.NEW);

        if (projectRecords.isEmpty()) {
            return Response.status(ErrorType.BAD_REQUEST.getCode()).build();
        }

        final String baseUrl = String.format("%s/games/%s/%s", Constants.WEBSITE_URL, gameSlug, projectTypeSlug);
        Feed feed = new Feed();
        feed.setId(URI.create(baseUrl + "/feed.atom"));
        feed.getLinks().add(new Link("self", baseUrl));
        feed.setUpdated(new Date());
        feed.setTitle("Project Feed");
        for (ProjectsEntity record : projectRecords) {
            Content content = new Content();
            content.setText(record.getSummary());
            Entry entry = new Entry();
            entry.setId(URI.create(baseUrl + "/" + record.getSlug()));
            entry.setTitle(record.getName());
            entry.setUpdated(new Date(record.getUpdatedAt().getTime()));
            entry.setContent(content);

            //TODO Maybe show all authors?
            entry.getAuthors().add(new Person(record.getOwner().getDisplayName()));
            feed.getEntries().add(entry);
        }

        return Response.ok(feed).build();
    }

    @Cache(maxAge = 30, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}")
    public Response getProject (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug) {

        final ProjectsEntity projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null || !projectRecord.isReleased() && token == null) {
            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {

                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }

            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        if (token != null) {
            List<String> permissions = ProjectPermissions.getAuthorizedUserPermissions(projectRecord, token);
            if (permissions != null) {
                return ResponseUtil.successResponse(new DataProjectAuthorized(projectRecord, permissions));
            }
        }

        return ResponseUtil.successResponse(new DataProject(projectRecord));
    }

    @PATCH
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}")
    public Response patchProject (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug, @MultipartForm ProjectForm form) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {
            return ErrorMessage.NOT_FOUND_GAME.respond();
        }

        if (DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug) == null) {
            return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
        }

        ProjectsEntity projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null) {
            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        if (!ProjectPermissions.hasPermission(projectRecord, token, ProjectPermissions.PROJECT_EDIT)) {

            return ErrorMessage.USER_NOT_AUTHORIZED.respond();
        }

        long projectId = projectRecord.getId();

        String newName = projectRecord.getName();
        if (!GenericValidator.isBlankOrNull(form.name)) {
            String name = form.name.trim();
            if (!name.equals(newName)) {
                if (!Validator.validateProjectName(name)) {
                    return ErrorMessage.PROJECT_INVALID_NAME.respond();
                }

                newName = name;
            }
        }

        String newSummary = projectRecord.getSummary();
        if (!GenericValidator.isBlankOrNull(form.summary)) {
            String summary = form.summary.trim();
            if (!summary.equals(newSummary)) {
                if (!Validator.validateProjectSummary(summary)) {
                    return ErrorMessage.PROJECT_INVALID_SUMMARY.respond();
                }

                newSummary = summary;
            }
        }

        String newDescription = projectRecord.getDescription();
        if (!GenericValidator.isBlankOrNull(form.description)) {
            String description = form.description.trim();
            if (!description.equals(newDescription)) {
                if (!Validator.validateProjectDescription(description)) {
                    return ErrorMessage.PROJECT_INVALID_DESCRIPTION.respond();
                }

                newDescription = description;
            }
        }

        //TODO
//        if (!DATABASE.projectDAO.updateProject(projectId, newName, newSummary, newDescription)) {
//            return ErrorMessage.FAILED_UPDATE_PROJECT.respond();
//        }

        //TODO Tags
//        Set<String> tags = form.getTags();
//        List<TagRecord> tagRecords = Validator.validateTags(gameSlug, projectTypeSlug, tags);
//        if (tagRecords.size() != tags.size()) {
//            return ErrorMessage.PROJECT_INVALID_TAGS.respond();
//        }
//
//
//        List<Long> tagIds = tagRecords.stream().map(TagRecord::getId).collect(Collectors.toList());
//        if (!DATABASE.projectDAO.updateProjectTags(projectId, tagIds)) {
//            return ErrorMessage.FAILED_CREATE_PROJECT_TAGS.respond();
//        }

        if (form.logo != null) {
            final BufferedImage image = ImageUtil.isValidImage(form.logo, 1000000L);

            if (image == null) {
                return ErrorMessage.PROJECT_INVALID_LOGO.respond();
            }

            final File file = new File(Constants.CDN_FOLDER, String.format("games/%s/%s/%d/logo.png", gameSlug, projectTypeSlug, projectId));
            if (!ImageUtil.saveImage(image, file)) {
                return ErrorMessage.ERROR_SAVING_IMAGE.respond();
            }
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Cache(maxAge = 30, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}/feed.atom")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response getProjectFileFeed (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug) {

        final ProjectsEntity projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
        if (projectRecord == null) {
            return Response.status(ErrorType.BAD_REQUEST.getCode()).build();
        }

        final List<ProjectFilesEntity> projectFileRecords = DATABASE.fileDAO.findAllByProjectId(projectRecord.getId(), false, 1, 25, ProjectFileSort.NEW, null);

        final String baseUrl = String.format("%s/games/%s/%s/%s", Constants.WEBSITE_URL, gameSlug, projectTypeSlug, projectSlug);
        Feed feed = new Feed();
        feed.setId(URI.create(baseUrl + "/feed.atom"));
        feed.getLinks().add(new Link("self", baseUrl + "/"));
        feed.setUpdated(new Date());
        feed.setTitle(projectRecord.getName() + " File Feed");
        for (ProjectFilesEntity record : projectFileRecords) {
            Content content = new Content();
            content.setText(record.getChangelog());
            Entry entry = new Entry();
            entry.setId(URI.create(baseUrl + "/files/" + record.getId()));
            entry.setTitle(record.getName());
            entry.setUpdated(new Date(record.getUpdatedAt().getTime()));
            entry.setContent(content);
            entry.getAuthors().add(new Person(record.getUser().getDisplayName()));
            feed.getEntries().add(entry);
        }

        return Response.ok(feed).build();
    }

    @Cache(maxAge = 60, mustRevalidate = true)
    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}/files")
    public Response getProjectFiles (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug, @Query ProjectFileQuery query) {

        long page = query.getPage();
        int limit = query.getLimit();
        Sort sort = query.getSort(ProjectFileSort.NEW);
        String version = query.getVersions();

        final ProjectsEntity projectRecord = DATABASE.projectDAO.findOneProjectByGameSlugAndProjectTypeSlugAndProjectSlug(gameSlug, projectTypeSlug, projectSlug);
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
        final List<ProjectFilesEntity> projectFileRecords = DATABASE.fileDAO.findAllByProjectId(projectRecord.getId(), authorized, page, limit, sort, version);

        final List<DataProjectFile> projectFiles = projectFileRecords.stream().map(record -> record.isReleased() ?
            new DataProjectFileAvailable(record, gameSlug, projectTypeSlug, projectSlug) :
            new DataProjectFileInQueue(record, gameSlug, projectTypeSlug, projectSlug)).collect(Collectors.toList());
        return ResponseUtil.successResponse(projectFiles);
    }

    @POST
    @Path("/{gameSlug}/{projectTypeSlug}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postProject (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @MultipartForm ProjectForm form) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        ProjectTypesEntity projectType = DATABASE.projectDAO.findOneProjectTypeByGameSlugAndProjectTypeSlug(gameSlug, projectTypeSlug);

        if (projectType == null) {
            if (DATABASE.gameDAO.findOneBySlug(gameSlug) == null) {
                return ErrorMessage.NOT_FOUND_GAME.respond();
            }
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

        String[] tags = form.getTags();
        List<TagsEntity> tagRecords = Validator.validateTags(projectType, tags);
        if (tagRecords.size() != tags.length) {
            return ErrorMessage.PROJECT_INVALID_TAGS.respond();
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
        ProjectsEntity project = new ProjectsEntity();
        project.setSlug(projectSlug);
        project.setName(name);
        project.setSummary(summary);
        project.setDescription(description);
        project.setOwner(new UsersEntity(token.getUserId()));
        project.setProjectType(projectType);
        if (!tagRecords.isEmpty()) {
            List<ProjectTagsEntity> tagIds = new ArrayList<>();
            for (TagsEntity tag : tagRecords) {
                ProjectTagsEntity projectTag = new ProjectTagsEntity();
                projectTag.setTag(tag);
                tagIds.add(projectTag);
            }
            project.setTags(tagIds);
        }

        if (!DATABASE.projectDAO.insertProject(project)) {
            return ErrorMessage.FAILED_CREATE_PROJECT.respond();
        }

        project = DATABASE.projectDAO.findOneProjectByProjectId(project.getId());
        if (project == null) {
            return ErrorMessage.NOT_FOUND_PROJECT.respond();
        }

        final File file = new File(Constants.CDN_FOLDER, String.format("games/%s/%s/%d/logo.png", gameSlug, projectTypeSlug, project.getId()));
        if (!ImageUtil.saveImage(image, file)) {
            return ErrorMessage.ERROR_SAVING_IMAGE.respond();
        }

        return ResponseUtil.successResponse(new DataProject(project));
    }
}
