package com.diluv.api.utils.query;

import javax.ws.rs.QueryParam;

import org.apache.commons.validator.GenericValidator;

import com.diluv.confluencia.database.sort.ProjectSort;
import com.diluv.confluencia.database.sort.Sort;

public class ProjectQuery extends PaginationQuery {

    @QueryParam("version")
    public String version;

    @QueryParam("search")
    private String search;

    @Override
    public Sort getSort (Sort defaultSort) {

        for (Sort b : ProjectSort.LIST) {
            if (b.getSlug().equalsIgnoreCase(this.sort)) {
                return b;
            }
        }
        return defaultSort;
    }

    public String getSearch () {

        if (GenericValidator.isBlankOrNull(this.search)) {
            return "%%";
        }
        return "%" + this.search + "%";
    }
}
