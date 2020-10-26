package com.diluv.api.v1.admin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.validator.GenericValidator;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.spi.CorsHeaders;

import com.diluv.api.data.DataGame;
import com.diluv.api.graphql.GameResolver;
import com.diluv.api.graphql.LoaderResolver;
import com.diluv.api.graphql.Mutation;
import com.diluv.api.graphql.ProjectResolver;
import com.diluv.api.graphql.ProjectTypeResolver;
import com.diluv.api.graphql.Query;
import com.diluv.api.graphql.TagResolver;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.ImageUtil;
import com.diluv.api.utils.auth.tokens.ErrorToken;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.UserPermissions;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.api.v1.games.GamesAPI;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.GamesEntity;
import com.diluv.confluencia.database.record.ProjectTypesEntity;
import graphql.Scalars;
import graphql.kickstart.servlet.GraphQLHttpServlet;
import graphql.kickstart.tools.SchemaParser;
import graphql.kickstart.tools.SchemaParserOptions;
import graphql.schema.GraphQLSchema;

@GZIP
@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminAPI {

    private static HttpServlet delegateServlet;

    public static void init () {

        SchemaParserOptions options = SchemaParserOptions
            .newOptions()
//            .fieldVisibility(DefaultGraphqlFieldVisibility.DEFAULT_FIELD_VISIBILITY)
            .build();

        GraphQLSchema schema = SchemaParser.newParser()
            .file("diluv.graphqls")
            .resolvers(new Query(),
                new Mutation(),
                new ProjectTypeResolver(),
                new ProjectResolver(),
                new TagResolver(),
                new LoaderResolver(),
                new GameResolver())
            .scalars(Scalars.GraphQLLong)
            .options(options)
            .build()
            .makeExecutableSchema();

        delegateServlet = GraphQLHttpServlet.with(schema);
    }

    @POST
    @Path("/games")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postGames (@HeaderParam("Authorization") Token token, @MultipartForm AdminGameForm form) {

        return Confluencia.getTransaction(session -> {
            Response permission = hasPermission(token);
            if (permission != null) {
                return permission;
            }

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
    public Response patchGames (@HeaderParam("Authorization") Token token, @MultipartForm AdminGameForm form) {

        return Confluencia.getTransaction(session -> {
            Response permission = hasPermission(token);
            if (permission != null) {
                return permission;
            }

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

            return Response.status(Response.Status.NO_CONTENT).build();
        });
    }

    @GET
    @Path("/graphql")
    public Response getGraphQL (@HeaderParam("Authorization") Token token, @Context HttpServletRequest req, @Context HttpServletResponse resp) {

        return request(token, req, resp);
    }

    @POST
    @Path("/graphql")
    public Response postGraphQL (@HeaderParam("Authorization") Token token, @Context HttpServletRequest req, @Context HttpServletResponse resp) {

        return request(token, req, resp);
    }

    public Response hasPermission (Token token) {

        if (token instanceof ErrorToken) {
            return ((ErrorToken) token).getResponse();
        }

        if (token.isApiToken()) {
            return ErrorMessage.USER_INVALID_TOKEN.respond("Can't use an API token for this request");
        }

        if (!UserPermissions.hasPermission(token, UserPermissions.VIEW_ADMIN)) {
            return ErrorMessage.USER_NOT_AUTHORIZED.respond();
        }

        return null;
    }

    public Response request (Token token, HttpServletRequest req, HttpServletResponse resp) {

        Response permission = hasPermission(token);
        if (permission != null) {
            return permission;
        }
        try {

            resp.addHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, req.getHeader(CorsHeaders.ORIGIN));
            resp.addHeader(CorsHeaders.VARY, CorsHeaders.ORIGIN);
            resp.addHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            delegateServlet.service(req, resp);
        }
        catch (ServletException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
