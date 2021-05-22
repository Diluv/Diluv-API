package com.diluv.api.v1.admin;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
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
import javax.ws.rs.core.StreamingOutput;

import com.diluv.api.utils.auth.RequireToken;

import org.apache.commons.io.FileUtils;
import org.apache.commons.validator.GenericValidator;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.diluv.api.data.DataGame;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.FileUtil;
import com.diluv.api.utils.ImageUtil;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.UserPermissions;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.api.v1.games.GamesAPI;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.GamesEntity;
import com.diluv.confluencia.database.record.ProjectFilesEntity;
import com.diluv.confluencia.database.record.ProjectTypesEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;

@ApplicationScoped
@GZIP
@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminAPI {

    @POST
    @Path("/games")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postGames (@RequireToken (apiToken = false, userPermissions = {UserPermissions.VIEW_ADMIN}) @HeaderParam("Authorization") Token token, @MultipartForm AdminGameForm form) {

        return Confluencia.getTransaction(session -> {

            if (form.data == null) {
                return ErrorMessage.INVALID_DATA.respond();
            }

            AdminGameData data = form.data;

            if (GenericValidator.isBlankOrNull(data.slug)) {
                return ErrorMessage.INVALID_DATA.respond("The game slug is required.");
            }
            if (GenericValidator.isBlankOrNull(data.name)) {
                return ErrorMessage.INVALID_DATA.respond("The game name is required.");
            }
            if (GenericValidator.isBlankOrNull(data.url)) {
                return ErrorMessage.INVALID_DATA.respond("The game store/website url is required");
            }
            if (GenericValidator.isBlankOrNull(data.projectTypeSlug)) {
                return ErrorMessage.INVALID_DATA.respond("The default project type slug is required");
            }
            if (GenericValidator.isBlankOrNull(data.projectTypeName)) {
                return ErrorMessage.INVALID_DATA.respond("The default project type name is required");
            }

            if (Confluencia.GAME.findOneBySlug(session, data.slug) != null) {
                return ErrorMessage.INVALID_DATA.respond();
            }

            if (form.logoPNG == null) {
                return ErrorMessage.REQUIRES_IMAGE.respond();
            }

            final BufferedImage imagePNG = ImageUtil.isValidImage(form.logoPNG, 1000000L);

            if (imagePNG == null) {
                return ErrorMessage.INVALID_IMAGE.respond();
            }

            File logoPath = new File(Constants.CDN_FOLDER, "games/" + data.slug);
            logoPath.mkdirs();

            if (!ImageUtil.savePNG(imagePNG, new File(logoPath, "logo.png"))) {
                // return ErrorMessage.ERROR_SAVING_IMAGE.respond();
                return ErrorMessage.THROWABLE.respond();
            }

            GamesEntity game = new GamesEntity();
            game.setSlug(data.slug);
            game.setName(data.name);
            game.setUrl(data.url);

            ProjectTypesEntity projectType = new ProjectTypesEntity();
            projectType.setSlug(data.projectTypeSlug);
            projectType.setName(data.projectTypeName);

            game.addProjectType(projectType);
            game.setDefaultProjectTypeEntity(data.projectTypeSlug);

            session.save(game);

            game = Confluencia.GAME.findOneBySlug(session, data.slug);

            if (game == null) {
                return ErrorMessage.NOT_FOUND_GAME.respond();
            }
            return ResponseUtil.successResponse(new DataGame(game, GamesAPI.PROJECT_SORTS, 0L));
        });
    }

    @PATCH
    @Path("/games")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response patchGames (@RequireToken (apiToken = false, userPermissions = {UserPermissions.VIEW_ADMIN})  @HeaderParam("Authorization") Token token, @MultipartForm AdminGameForm form) {

        return Confluencia.getTransaction(session -> {
            if (form.data == null) {
                return ErrorMessage.INVALID_DATA.respond();
            }

            AdminGameData data = form.data;

            if (GenericValidator.isBlankOrNull(data.slug)) {
                return ErrorMessage.INVALID_DATA.respond("The game slug is required.");
            }

            GamesEntity game = Confluencia.GAME.findOneBySlug(session, data.slug);

            if (game == null) {
                return ErrorMessage.NOT_FOUND_GAME.respond();
            }

            if (!GenericValidator.isBlankOrNull(data.name)) {
                game.setName(data.name);
            }
            if (!GenericValidator.isBlankOrNull(data.url)) {
                game.setUrl(data.url);
            }

            File logoPath = new File(Constants.CDN_FOLDER, "games/" + data.slug);
            logoPath.mkdirs();

            if (form.logoPNG != null) {
                final BufferedImage imagePNG = ImageUtil.isValidImage(form.logoPNG, 1000000L);

                if (imagePNG == null) {
                    return ErrorMessage.INVALID_IMAGE.respond();
                }
                if (!ImageUtil.savePNG(imagePNG, new File(logoPath, "logo.png"))) {
                    // return ErrorMessage.ERROR_SAVING_IMAGE.respond();
                    return ErrorMessage.THROWABLE.respond();
                }
            }

            session.update(game);

            return ResponseUtil.noContent();
        });
    }

    @GET
    @Path("/files/{fileId}")
    public Response getProjectFile (@RequireToken (apiToken = false, userPermissions = {UserPermissions.VIEW_ADMIN}) @HeaderParam("Authorization") Token token, @PathParam("fileId") long fileId) {

        ProjectFilesEntity rs = Confluencia.getTransaction(session -> {
            return Confluencia.FILE.findOneById(session, fileId);
        });

        if (rs.isReleased()) {
            //TODO ERROR
            return ResponseUtil.noContent();
        }

        final ProjectsEntity p = rs.getProject();

        File destination = FileUtil.getOutputLocation(p.getGame().getSlug(), p.getProjectType().getSlug(), p.getId(), rs.getId(), rs.getName());
        if (!destination.exists()) {
            return ResponseUtil.noContent();
        }

        StreamingOutput stream = os -> FileUtils.copyFile(destination, os);
        return Response.ok(stream).header("content-disposition", "attachment; filename=\"" + rs.getName() + "\"").build();
    }
}
