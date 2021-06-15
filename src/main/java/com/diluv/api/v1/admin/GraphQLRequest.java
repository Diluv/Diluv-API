package com.diluv.api.v1.admin;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

public class GraphQLRequest {

    @Expose
    private String query;

    @Expose
    private String operationName;

    @Expose
    private Map<String, Object> variables;

    public GraphQLRequest (String query, String operationName) {

        this(query, operationName, new HashMap<>());
    }

    public GraphQLRequest (String query, String operationName, Map<String, Object> variables) {

        this.query = query;
        this.operationName = operationName;
        this.variables = variables;
    }

    public String getQuery () {

        return this.query;
    }

    public String getOperationName () {

        return this.operationName;
    }

    public Map<String, Object> getVariables () {

        if (this.variables == null) {
            return new HashMap<>();
        }
        return this.variables;
    }
}
