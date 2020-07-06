package com.diluv.api.utils.query;

import javax.ws.rs.QueryParam;

import com.diluv.confluencia.database.sort.ProjectFileSort;
import com.diluv.confluencia.database.sort.ProjectSort;
import com.diluv.confluencia.database.sort.Sort;

public class AuthorProjectsQuery extends PaginationQuery {

    @Override
    public Sort getSort (Sort defaultSort) {

        for (Sort b : ProjectSort.LIST) {
            if (b.getSlug().equalsIgnoreCase(this.sort)) {
                return b;
            }
        }
        return defaultSort;
    }

}
