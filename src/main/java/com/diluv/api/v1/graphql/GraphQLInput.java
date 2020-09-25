package com.diluv.api.v1.graphql;

import java.util.Collections;
import java.util.Map;

public class GraphQLInput {

    private String query;
    private String operationName;
    private Map<String, Object> variables;

    public String getQuery () {

        return this.query;
    }

    public String getOperationName () {

        return this.operationName;
    }

    public Map<String, Object> getVariables () {

        if (this.variables == null) {
            return Collections.emptyMap();
        }

        return this.variables;
    }
}
