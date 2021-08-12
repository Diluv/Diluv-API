package com.diluv.api.v1.admin;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import com.diluv.api.graphql.GameResolver;
import com.diluv.api.graphql.LoaderResolver;
import com.diluv.api.graphql.Mutation;
import com.diluv.api.graphql.ProjectFileResolver;
import com.diluv.api.graphql.ProjectResolver;
import com.diluv.api.graphql.ProjectTypeResolver;
import com.diluv.api.graphql.Query;
import com.diluv.api.graphql.RegistrationCodesResolver;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.ExecutionId;
import graphql.introspection.IntrospectionQuery;
import graphql.kickstart.tools.SchemaParser;
import graphql.kickstart.tools.SchemaParserOptions;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;

public class GraphQLUtil {

    private static GraphQL graphql;

    private static GraphQL getGraphQL () {

        if (graphql != null) {
            return graphql;
        }
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
                new GameResolver(),
                new RegistrationCodesResolver())
            .scalars(ExtendedScalars.GraphQLLong)
            .options(options)
            .build()
            .makeExecutableSchema();


        return graphql = GraphQL.newGraphQL(schema).build();
    }

    public static Response getResponse (GraphQLRequest request, Map<String, Object> context) {

        ExecutionResult s = getGraphQL().execute(ExecutionInput.newExecutionInput()
            .query(request.getQuery())
            .operationName(request.getOperationName())
            .graphQLContext(context)
            .root(context)
            .variables(request.getVariables())
            .executionId(ExecutionId.generate())
            .build());

        return Response.ok(s.toSpecification()).build();
    }

    public static Response getIntrospection () {

        return getResponse(new GraphQLRequest(IntrospectionQuery.INTROSPECTION_QUERY, "IntrospectionQuery"), new HashMap<>());
    }
}
