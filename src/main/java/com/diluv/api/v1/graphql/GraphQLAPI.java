package com.diluv.api.v1.graphql;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

import com.diluv.api.graphql.GameResolver;
import com.diluv.api.graphql.LoaderResolver;
import com.diluv.api.graphql.Mutation;
import com.diluv.api.graphql.ProjectResolver;
import com.diluv.api.graphql.ProjectTypeResolver;
import com.diluv.api.graphql.Query;
import com.diluv.api.graphql.TagResolver;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.UserPermissions;
import graphql.Scalars;
import graphql.kickstart.servlet.GraphQLHttpServlet;
import graphql.kickstart.servlet.apollo.ApolloScalars;
import graphql.kickstart.tools.SchemaParser;
import graphql.kickstart.tools.SchemaParserOptions;
import graphql.schema.GraphQLSchema;

@GZIP
@Path("/graphql")
@Produces(MediaType.APPLICATION_JSON)
public class GraphQLAPI {

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
            .scalars(Scalars.GraphQLLong, ApolloScalars.Upload)
            .options(options)
            .build()
            .makeExecutableSchema();


        delegateServlet = GraphQLHttpServlet.with(schema);
    }

    @GET
    public Response onGet (@HeaderParam("Authorization") Token token, @Context HttpServletRequest req, @Context HttpServletResponse resp) {

        return request(token, req, resp);
    }

    @POST
    public Response onPost (@HeaderParam("Authorization") Token token, @Context HttpServletRequest req, @Context HttpServletResponse resp) {

        return request(token, req, resp);
    }

    public Response request (Token token, HttpServletRequest req, HttpServletResponse resp) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

//        if (token == JWTUtil.INVALID || token.isApiToken()) {
//            return ErrorMessage.USER_INVALID_TOKEN.respond();
//        }

        if (!UserPermissions.hasPermission(token, UserPermissions.VIEW_ADMIN)) {
            return ErrorMessage.USER_NOT_AUTHORIZED.respond();
        }
        try {
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
