package com.diluv.api.v1.graphql;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

import com.diluv.api.graphql.ProjectResolver;
import com.diluv.api.graphql.Query;
import com.diluv.api.utils.auth.JWTUtil;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.UserPermissions;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.kickstart.tools.SchemaParser;
import graphql.schema.GraphQLSchema;

@GZIP
@Path("/graphql")
@Produces(MediaType.APPLICATION_JSON)
public class GraphQLAPI {

    private final GraphQL graphQL;

    public GraphQLAPI () {

        GraphQLSchema schema = SchemaParser.newParser()
            .file("diluv.graphqls")
            .resolvers(new Query(), new ProjectResolver())
            .build()
            .makeExecutableSchema();


        graphQL = GraphQL.newGraphQL(schema)
            .build();
    }

    @POST
    public Response execute (@HeaderParam("Authorization") Token token, GraphQLInput data) {

        if (token == null) {
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }

        if (token == JWTUtil.INVALID) {
            return ErrorMessage.USER_INVALID_TOKEN.respond();
        }

        if (!UserPermissions.hasPermission(token, UserPermissions.VIEW_ADMIN)) {
            return ErrorMessage.USER_NOT_AUTHORIZED.respond();
        }

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
            .query(data.getQuery())
            .operationName(data.getOperationName())
            .context(data)
            .root(data)
            .variables(data.getVariables())
            .build();
        ExecutionResult executionResult = graphQL.execute(executionInput);
        Map<String, Object> result = new HashMap<>();
        if (!executionResult.getErrors().isEmpty()) {
            result.put("errors", executionResult.getErrors());
//            logger.error("Errors: {}", executionResult.getErrors());
        }
        else {
            result.put("data", executionResult.getData());
        }
        return Response
            .ok(result)
            .build();
    }
}
