package com.diluv.api.v1.games;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.Query;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.diluv.api.data.DataBaseProject;
import com.diluv.api.data.DataGame;
import com.diluv.api.data.DataGameList;
import com.diluv.api.data.DataGameVersion;
import com.diluv.api.data.DataProject;
import com.diluv.api.data.DataProjectList;
import com.diluv.api.data.DataProjectType;
import com.diluv.api.data.DataSlugName;
import com.diluv.api.data.DataUploadType;
import com.diluv.api.data.feed.FeedProjectFiles;
import com.diluv.api.data.feed.FeedProjects;
import com.diluv.api.data.site.DataSiteProjectFileDisplay;
import com.diluv.api.data.site.DataSiteProjectFilesPage;
import com.diluv.api.provider.ResponseException;
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
import com.diluv.api.utils.validator.RequireToken;
import com.diluv.api.v1.utilities.ProjectFileService;
import com.diluv.api.v1.utilities.ProjectService;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.*;
import com.diluv.confluencia.database.sort.GameSort;
import com.diluv.confluencia.database.sort.ProjectFileSort;
import com.diluv.confluencia.database.sort.ProjectSort;
import com.diluv.confluencia.database.sort.Sort;
import com.github.slugify.Slugify;

@ApplicationScoped
@GZIP
@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
public class GamesAPI {

    private static final List<String> ROLES = Arrays.asList("Project Lead", "Artist", "Developer", "Mascot", "Documentation", "Translation");

    private final Slugify slugify = new Slugify();

    @GET
    @Path("/")
    public Response getGames (@Query GameQuery query) {

        final long page = query.getPage();
        final int limit = query.getLimit();
        final Sort sort = query.getSort(GameSort.NAME);
        final String search = query.getSearch();

        return Confluencia.getTransaction(session -> {
            final List<DataSlugName> games = Confluencia.GAME.findAll(session, page, limit, sort, search)
                .stream()
                .map(DataGame::new)
                .collect(Collectors.toList());

            final long gameCount = Confluencia.GAME.countAllBySearch(session, search);

            return ResponseUtil.successResponse(new DataGameList(games, gameCount));
        });
    }

    @GET
    @Path("/{gameSlug}")
    public Response getGame (@PathParam("gameSlug") String gameSlug) {

        return Confluencia.getTransaction(session -> {
            final GamesEntity gameRecord = Confluencia.GAME.findOneBySlug(session, gameSlug);

            if (gameRecord == null) {

                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            final long projectCount = Confluencia.PROJECT.countAllByGameSlug(session, gameSlug);

            return ResponseUtil.successResponse(new DataGame(gameRecord, projectCount));
        });
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}")
    public Response getProjectType (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug) {

        return Confluencia.getTransaction(session -> {
            final ProjectTypesEntity projectTypesRecords = Confluencia.PROJECT.findOneProjectTypeByGameSlugAndProjectTypeSlug(session, gameSlug, projectTypeSlug);

            if (projectTypesRecords == null) {

                if (Confluencia.GAME.findOneBySlug(session, gameSlug) == null) {

                    return ErrorMessage.NOT_FOUND_GAME.respond();
                }

                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }

            final long projectCount = Confluencia.PROJECT.countAllByGameSlugAndProjectTypeSlug(session, gameSlug, projectTypeSlug);
            return ResponseUtil.successResponse(new DataProjectType(projectTypesRecords, projectCount));
        });
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/projects")
    public Response getProjects (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @Query ProjectQuery query) {

        final long page = query.getPage();
        final int limit = query.getLimit();
        final Sort sort = query.getSort(ProjectSort.POPULAR);
        final String search = query.getSearch();
        final String versions = query.getVersions();
        final Set<String> tags = query.getTags();
        final Set<String> loaders = query.getLoaders();

        return Confluencia.getTransaction(session -> {

            final List<ProjectsEntity> projects = Confluencia.PROJECT.findAllByGameAndProjectType(session, gameSlug, projectTypeSlug, search, page, limit, sort, versions, tags, loaders);

            if (projects.isEmpty()) {

                if (Confluencia.GAME.findOneBySlug(session, gameSlug) == null) {

                    return ErrorMessage.NOT_FOUND_GAME.respond();
                }

                if (Confluencia.PROJECT.findOneProjectTypeByGameSlugAndProjectTypeSlug(session, gameSlug, projectTypeSlug) == null) {

                    return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
                }
            }

            final List<DataBaseProject> dataProjects = projects.stream().map(DataBaseProject::new).collect(Collectors.toList());

            return ResponseUtil.successResponse(new DataProjectList(dataProjects));
        });
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/feed.atom")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response getProjectFeed (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug) {

        final List<ProjectsEntity> projects = Confluencia.getTransaction(session -> {
            return Confluencia.PROJECT.findAllByGameAndProjectType(session, gameSlug, projectTypeSlug, "", 1, 25, ProjectSort.NEW);
        });

        if (projects.isEmpty()) {
            return Response.status(ErrorType.NOT_FOUND.getCode()).build();
        }

        final String baseUrl = String.format("%s/games/%s/%s", Constants.WEBSITE_URL, gameSlug, projectTypeSlug);
        FeedProjects feed = new FeedProjects(baseUrl);

        projects.forEach(feed::addProject);

        return Response.ok(feed).build();
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}")
    public Response getProject (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug) throws ResponseException {

        return Confluencia.getTransaction(session -> {
            try {
                final DataBaseProject project = ProjectService.getDataProject(session, gameSlug, projectTypeSlug, projectSlug, token);
                return ResponseUtil.successResponse(project);
            }
            catch (ResponseException e) {
                return e.getResponse();
            }
        });
    }

    @DELETE
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}")
    public Response deleteProject (@RequireToken @HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug) {

        return Confluencia.getTransaction(session -> {
            try {
                final ProjectsEntity project = ProjectService.getProject(session, gameSlug, projectTypeSlug, projectSlug, token);
                if (project.getOwner().getId() == token.getUserId()) {
                    session.delete(project);
                    return ResponseUtil.noContent();
                }
                else {
                    return ErrorMessage.USER_NOT_AUTHORIZED.respond();
                }
            }
            catch (ResponseException e) {
                return e.getResponse();
            }
        });
    }

    @PATCH
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}")
    public Response patchProject (@RequireToken @HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug, @Valid @MultipartForm ProjectPatchForm form) {

        return Confluencia.getTransaction(session -> {

            try {
                ProjectsEntity project = ProjectService.getProject(session, gameSlug, projectTypeSlug, projectSlug, token);

                if (!ProjectPermissions.hasPermission(project, token, ProjectPermissions.PROJECT_EDIT)) {

                    return ErrorMessage.USER_NOT_AUTHORIZED.respond();
                }

                if (form.data != null) {
                    if (!GenericValidator.isBlankOrNull(form.data.name)) {
                        String name = form.data.name.trim();
                        if (!name.equals(project.getName())) {
                            project.setName(name);
                        }
                    }

                    if (!GenericValidator.isBlankOrNull(form.data.summary)) {
                        String summary = form.data.summary.trim();
                        if (!summary.equals(project.getSummary())) {
                            project.setSummary(summary);
                        }
                    }

                    if (!GenericValidator.isBlankOrNull(form.data.description)) {
                        String description = form.data.description.trim();
                        if (!description.equals(project.getDescription())) {
                            project.setDescription(description);
                        }
                    }

                    List<String> tags = form.data.tags;
                    if (!tags.isEmpty()) {
                        List<TagsEntity> tagRecords = Validator.validateTags(project.getProjectType(), tags);
                        if (tagRecords.size() != tags.size()) {
                            return ErrorMessage.PROJECT_INVALID_TAGS.respond();
                        }

                        project.getTags().removeIf(p -> !tagRecords.contains(p.getTag()));

                        for (TagsEntity tag : tagRecords) {
                            if (project.getTags().stream().noneMatch(t -> t.getTag() == tag)) {
                                ProjectTagsEntity tagsEntity = new ProjectTagsEntity();
                                tagsEntity.setTag(tag);
                                project.addTag(tagsEntity);
                            }
                        }
                    }

                    if (!form.data.authors.isEmpty()) {
                        List<Long> authorIds = form.data.authors.stream().map(projectAuthors -> projectAuthors.userId).collect(Collectors.toList());

                        if (authorIds.contains(project.getOwner().getId())) {
                            throw new ResponseException(ErrorMessage.PROJECT_OWNER.respond());
                        }

                        for (ProjectAuthors author : form.data.authors) {
                            String authorRole = ROLES.stream().filter(role -> role.equalsIgnoreCase(author.role)).findFirst().orElse(null);

                            if (authorRole == null) {
                                throw new ResponseException(ErrorMessage.INVALID_ROLE.respond());
                            }

                            if (!author.permissions.isEmpty() && !ProjectPermissions.validatePermissions(author.permissions)) {
                                throw new ResponseException(ErrorMessage.INVALID_PERMISSION.respond());
                            }

                            ProjectAuthorsEntity projectAuthorEntity = project.getAuthors().stream().filter(projectAuthorsEntity -> projectAuthorsEntity.getUser().getId() == author.userId).findAny().orElse(null);
                            if (projectAuthorEntity == null) {
                                UsersEntity user = Confluencia.USER.findOneByUserId(session, author.userId);
                                if (user == null) {
                                    throw new ResponseException(ErrorMessage.NOT_FOUND_USER.respond());
                                }

                                if (Confluencia.NOTIFICATION.existsProjectInviteWhereProjectIdAndUser(session, user.getId(), project.getId())) {
                                    throw new ResponseException(ErrorMessage.PROJECT_PENDING_INVITE.respond());
                                }

                                UsersEntity sender = Confluencia.USER.findOneByUserId(session, token.getUserId());

                                NotificationProjectInvitesEntity projectInvitesEntity = new NotificationProjectInvitesEntity();
                                projectInvitesEntity.setText(sender.getDisplayName() + " has invited you to join " + project.getName());
                                projectInvitesEntity.setUser(user);
                                projectInvitesEntity.setProject(project);
                                projectInvitesEntity.setStatus(NotificationProjectInvitesStatus.PENDING);
                                projectInvitesEntity.setSender(sender);
                                projectInvitesEntity.setRole(authorRole);
                                if (!author.permissions.isEmpty()) {
                                    author.permissions.forEach(projectInvitesEntity::addPermission);
                                }
                                session.persist(projectInvitesEntity);
                            }
                            else {
                                if (!projectAuthorEntity.getRole().equalsIgnoreCase(authorRole)) {
                                    projectAuthorEntity.setRole(authorRole);
                                }

                                if (!author.permissions.isEmpty()) {
                                    List<ProjectAuthorPermissionsEntity> removePermissions = new ArrayList<>();
                                    projectAuthorEntity.getPermissions().forEach(permissionsEntity -> {
                                        if (author.permissions.contains(permissionsEntity.getPermission())) {
                                            author.permissions.remove(permissionsEntity.getPermission());
                                        }
                                        else {
                                            removePermissions.add(permissionsEntity);
                                        }
                                    });
                                    projectAuthorEntity.getPermissions().removeAll(removePermissions);
                                    author.permissions.forEach(projectAuthorEntity::addPermission);
                                }
                            }
                        }
                    }

                    if (form.data.ownerId != null) {
                        if (form.data.ownerId != project.getOwner().getId()) {
                            ProjectAuthorsEntity newOwner = project.getAuthors().stream().filter(entity -> form.data.ownerId == entity.getUser().getId()).findFirst().orElse(null);
                            if (newOwner == null) {
                                UsersEntity usersEntity = Confluencia.USER.findOneByUserId(session, form.data.ownerId);
                                if (usersEntity == null) {
                                    return ErrorMessage.NOT_FOUND_USER.respond();
                                }
                                return ErrorMessage.PROJECT_NOT_MEMBER.respond();
                            }

                            ProjectAuthorsEntity oldOwner = new ProjectAuthorsEntity(project.getOwner(), "Retired");
                            project.addAuthor(oldOwner);
                            ProjectPermissions.getAllPermissions().forEach(oldOwner::addPermission);

                            project.getAuthors().remove(newOwner);
                            project.setOwner(newOwner.getUser());
                        }
                    }

                    if (form.data.resolveReview) {
                        for (ProjectReviewEntity review : project.getReviews()) {
                            if (!review.isCompleted()) {
                                project.setReview(true);
                                review.setCompleted(true);
                            }
                        }
                    }
                }

                session.update(project);

                if (form.logo != null) {
                    final BufferedImage image = ImageUtil.isValidImage(form.logo, FileUtils.ONE_MB);

                    if (image == null) {
                        return ErrorMessage.INVALID_IMAGE.respond();
                    }

                    final File file = new File(Constants.CDN_FOLDER, String.format("games/%s/%s/%d/logo.png", gameSlug, projectTypeSlug, project.getId()));
                    if (!ImageUtil.savePNG(image, file)) {
                        // return ErrorMessage.ERROR_SAVING_IMAGE.respond();
                        return ErrorMessage.THROWABLE.respond();
                    }
                }

                return ResponseUtil.noContent();
            }
            catch (ResponseException e) {
                return e.getResponse();
            }
        });
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}/feed.atom")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response getProjectFileFeed (@PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug) {

        return Confluencia.getTransaction(session -> {
            try {
                final ProjectsEntity project = ProjectService.getProject(session, gameSlug, projectTypeSlug, projectSlug, null);

                final List<ProjectFilesEntity> projectFiles = Confluencia.FILE.findAllByProject(session, project.getId(), false, 1, 25, ProjectFileSort.NEW, null, "");

                final String baseUrl = String.format("%s/games/%s/%s/%s", Constants.WEBSITE_URL, gameSlug, projectTypeSlug, projectSlug);
                FeedProjectFiles feed = new FeedProjectFiles(baseUrl, project.getName());
                projectFiles.forEach(feed::addFile);

                return Response.ok(feed).build();
            }
            catch (ResponseException e) {
                return Response.status(ErrorType.NOT_FOUND.getCode()).build();
            }
        });
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/upload")
    public Response getUploadTypeData (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug) {

        return Confluencia.getTransaction(session -> {
            try {
                final ProjectTypesEntity projectType = Confluencia.PROJECT.findOneProjectTypeByGameSlugAndProjectTypeSlug(session, gameSlug, projectTypeSlug);
                if (projectType == null) {
                    if (Confluencia.GAME.findOneBySlug(session, gameSlug) == null) {
                        throw new ResponseException(ErrorMessage.NOT_FOUND_GAME.respond());
                    }
                    throw new ResponseException(ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond());
                }

                // TODO Move to DB
                final List<DataSlugName> filters = new ArrayList<>();
                if ("minecraft-je".equalsIgnoreCase(gameSlug)) {
                    filters.add(new DataSlugName("snapshot", "Snapshot"));
                }
                final List<DataSlugName> loaders = projectType.getProjectTypeLoaders().stream().map(p -> new DataSlugName(p.getSlug(), p.getName())).collect(Collectors.toList());
                final List<DataGameVersion> gameVersions = projectType.getGame().getGameVersions().stream().map(DataGameVersion::new).collect(Collectors.toList());
                Set<DataSlugName> releaseTypes = Validator.VALID_RELEASE_TYPES.stream().map(releaseType -> new DataSlugName(releaseType, StringUtils.capitalize(releaseType))).collect(Collectors.toSet());
                Set<DataSlugName> dependencyTypes = Validator.DEP_TYPES.stream().map(depType -> new DataSlugName(depType, StringUtils.capitalize(depType))).collect(Collectors.toSet());

                return ResponseUtil.successResponse(new DataUploadType(loaders, releaseTypes, Validator.VALID_CLASSIFIERS, gameVersions, filters, dependencyTypes));
            }
            catch (ResponseException e) {
                return e.getResponse();
            }
        });
    }

    @GET
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}/files")
    public Response getProjectFiles (@HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug, @Query ProjectFileQuery query) {

        long page = query.getPage();
        int limit = query.getLimit();
        String search = query.getSearch();
        Sort sort = query.getSort(ProjectFileSort.NEW);
        String gameVersion = query.getGameVersion();

        return Confluencia.getTransaction(session -> {
            try {
                final ProjectsEntity project = ProjectService.getProject(session, gameSlug, projectTypeSlug, projectSlug, token);
                final DataBaseProject dataProject = ProjectService.getBaseDataProject(project, token);

                boolean authorized = ProjectPermissions.hasPermission(project, token, ProjectPermissions.FILE_UPLOAD);
                final List<ProjectFilesEntity> projectFileRecords = Confluencia.FILE.findAllByProject(session, project.getId(), authorized, page, limit, sort, gameVersion, search);

                final List<DataSiteProjectFileDisplay> projectFiles = projectFileRecords.stream().map(record -> {
                    final List<GameVersionsEntity> gameVersionRecords = record.getGameVersions().stream().map(ProjectFileGameVersionsEntity::getGameVersion).collect(Collectors.toList());
                    List<DataGameVersion> gameVersions = gameVersionRecords.stream().map(DataGameVersion::new).collect(Collectors.toList());
                    return record.isReleased() ?
                        new DataSiteProjectFileDisplay(record, gameVersions) :
                        new DataSiteProjectFileDisplay(record, gameVersions);
                }).collect(Collectors.toList());

                final long fileCount = Confluencia.FILE.countByProjectParams(session, project.getId(), authorized, gameVersion, search);

                return ResponseUtil.successResponse(new DataSiteProjectFilesPage(dataProject, projectFiles, fileCount));
            }
            catch (ResponseException e) {
                return e.getResponse();
            }
        });
    }

    @Deprecated
    @POST
    @Path("/{gameSlug}/{projectTypeSlug}/{projectSlug}/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadProjectFile (@RequireToken @HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @PathParam("projectSlug") String projectSlug, @MultipartForm ProjectFileUploadForm form) {

        return Confluencia.getTransaction(session -> {
            try {
                ProjectsEntity project = ProjectService.getProject(session, gameSlug, projectTypeSlug, projectSlug, token);
                return ProjectFileService.postProjectFile(session, project, token, form);
            }
            catch (ResponseException e) {
                return e.getResponse();
            }
        });
    }

    @POST
    @Path("/{gameSlug}/{projectTypeSlug}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createProject (@RequireToken @HeaderParam("Authorization") Token token, @PathParam("gameSlug") String gameSlug, @PathParam("projectTypeSlug") String projectTypeSlug, @Valid @MultipartForm ProjectCreateForm form) {

        return Confluencia.getTransaction(session -> {

            ProjectTypesEntity projectType = Confluencia.PROJECT.findOneProjectTypeByGameSlugAndProjectTypeSlug(session, gameSlug, projectTypeSlug);

            if (projectType == null) {
                if (Confluencia.GAME.findOneBySlug(session, gameSlug) == null) {
                    return ErrorMessage.NOT_FOUND_GAME.respond();
                }
                return ErrorMessage.NOT_FOUND_PROJECT_TYPE.respond();
            }

            List<String> tags = form.data.tags;
            List<TagsEntity> tagRecords = Validator.validateTags(projectType, tags);
            if (tagRecords.size() != tags.size()) {
                return ErrorMessage.PROJECT_INVALID_TAGS.respond();
            }

            final BufferedImage image = ImageUtil.isValidImage(form.logo, FileUtils.ONE_MB);

            if (image == null) {
                return ErrorMessage.REQUIRES_IMAGE.respond();
            }

            String name = form.data.name.trim();
            String summary = form.data.summary.trim();
            String description = form.data.description.trim();

            final String projectSlug = this.slugify.slugify(name);

            if (Confluencia.PROJECT.findOneProject(session, gameSlug, projectTypeSlug, projectSlug) != null) {
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
                for (TagsEntity tag : tagRecords) {
                    ProjectTagsEntity projectTag = new ProjectTagsEntity();
                    projectTag.setTag(tag);
                    project.addTag(projectTag);
                }
            }

            session.save(project);
            session.flush();
            session.refresh(project);

            final File file = new File(Constants.CDN_FOLDER, String.format("games/%s/%s/%d/logo.png", gameSlug, projectTypeSlug, project.getId()));
            if (!ImageUtil.savePNG(image, file)) {
                // return ErrorMessage.ERROR_SAVING_IMAGE.respond();
                return ErrorMessage.THROWABLE.respond();
            }

            return ResponseUtil.successResponse(new DataProject(project));
        });
    }
}
