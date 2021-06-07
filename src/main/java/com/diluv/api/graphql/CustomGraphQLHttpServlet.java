package com.diluv.api.graphql;

import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.UserPermissions;
import com.diluv.api.utils.response.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import graphql.Scalars;
import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.kickstart.servlet.GraphQLHttpServlet;
import graphql.kickstart.tools.SchemaParser;
import graphql.kickstart.tools.SchemaParserOptions;
import graphql.schema.GraphQLSchema;

import org.jboss.resteasy.spi.CorsHeaders;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CustomGraphQLHttpServlet extends GraphQLHttpServlet {

    @Override
    protected GraphQLConfiguration getConfiguration () {

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
                new ProjectFileResolver(),
                new LoaderResolver(),
                new GameResolver())
            .scalars(Scalars.GraphQLLong)
            .options(options)
            .build()
            .makeExecutableSchema();

        return GraphQLConfiguration.with(schema).build();
    }

    @Override
    protected void service (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //TODO broken?
        ErrorResponse permission = hasPermission(JWTUtil.getToken(req.getHeader("Authorization")));
        if (permission != null) {
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().println(this.getGsonInstance().toJson(permission));
            resp.setStatus(401);
            return;
        }

        setAccessControlHeaders(req, resp);
        super.service(req, resp);
    }

    @Override
    protected void doOptions (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        super.doOptions(req, resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders (HttpServletRequest req, HttpServletResponse resp) {

        resp.setHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, req.getHeader(CorsHeaders.ORIGIN));
        resp.addHeader(CorsHeaders.VARY, CorsHeaders.ORIGIN);
        resp.addHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }

    public ErrorResponse hasPermission (Token token) {

        if (token == null) {
            ErrorMessage errorMessage = ErrorMessage.USER_REQUIRED_TOKEN;
            return new ErrorResponse(errorMessage.getType().getError(), errorMessage.getUniqueId(), errorMessage.getMessage());
        }

//        if (token instanceof ErrorToken) {
//            ErrorMessage errorMessage = ((ErrorToken) token).getErrorMessage();
//            return new ErrorResponse(errorMessage.getType().getError(), errorMessage.getUniqueId(), errorMessage.getMessage());
//        }

        if (token.isApiToken()) {
            ErrorMessage errorMessage = ErrorMessage.USER_INVALID_TOKEN;
            return new ErrorResponse(errorMessage.getType().getError(), errorMessage.getUniqueId(), "Can't use an API token for this request");
        }

        if (!UserPermissions.hasPermission(token, UserPermissions.VIEW_ADMIN)) {
            ErrorMessage errorMessage = ErrorMessage.USER_NOT_AUTHORIZED;
            return new ErrorResponse(errorMessage.getType().getError(), errorMessage.getUniqueId(), errorMessage.getMessage());
        }

        return null;
    }

    private Gson gson;

    private Gson getGsonInstance () {

        if (this.gson == null) {

            this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        }

        return this.gson;
    }
}
