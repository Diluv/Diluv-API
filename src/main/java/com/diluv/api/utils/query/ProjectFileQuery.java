package com.diluv.api.utils.query;

import javax.ws.rs.QueryParam;

import com.diluv.confluencia.database.sort.ProjectFileSort;
import com.diluv.confluencia.database.sort.Sort;

public class ProjectFileQuery extends PaginationQuery {

    @QueryParam("versions")
    private String versions;

    @Override
    public Sort getSort (Sort defaultSort) {

        for (Sort b : ProjectFileSort.LIST) {
            if (b.getSlug().equalsIgnoreCase(this.sort)) {
                return b;
            }
        }
        return defaultSort;
    }

    public String getVersions () {

        return this.versions;
    }
}
