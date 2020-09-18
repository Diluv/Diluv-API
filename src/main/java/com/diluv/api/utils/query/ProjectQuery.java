package com.diluv.api.utils.query;

import javax.ws.rs.QueryParam;

import org.apache.commons.validator.GenericValidator;

import com.diluv.confluencia.database.sort.ProjectSort;
import com.diluv.confluencia.database.sort.Sort;

public class ProjectQuery extends PaginationQuery {

    @QueryParam("versions")
    private String versions;

    @QueryParam("tags")
    private String[] tags;

    @QueryParam("search")
    private String search;

    @QueryParam("loaders")
    private String[] loaders;

    @Override
    public Sort getSort (Sort defaultSort) {

        for (Sort b : ProjectSort.LIST) {
            if (b.getSlug().equalsIgnoreCase(this.sort)) {
                return b;
            }
        }
        return defaultSort;
    }

    public String getVersions () {

        return this.versions;
    }

    public String[] getTags () {

        // Shouldn't happen but have it in case
        if (this.tags == null) {
            return new String[0];
        }
        return this.tags;
    }

    public String[] getLoaders () {

        // Shouldn't happen but have it in case
        if (this.loaders == null) {
            return new String[0];
        }
        return this.loaders;
    }

    public String getSearch () {

        if (GenericValidator.isBlankOrNull(this.search)) {
            return "";
        }
        return this.search;
    }
}
