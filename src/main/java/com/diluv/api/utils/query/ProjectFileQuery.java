package com.diluv.api.utils.query;

import javax.ws.rs.QueryParam;

import com.diluv.confluencia.database.sort.ProjectFileSort;
import com.diluv.confluencia.database.sort.Sort;

import org.apache.commons.validator.GenericValidator;

public class ProjectFileQuery extends PaginationQuery {

    @QueryParam("game_version")
    private String gameVersion;

    @QueryParam("search")
    private String search;

    @Override
    public Sort getSort (Sort defaultSort) {

        for (Sort b : ProjectFileSort.LIST) {
            if (b.getSlug().equalsIgnoreCase(this.sort)) {
                return b;
            }
        }
        return defaultSort;
    }

    public String getGameVersion () {

        return GenericValidator.isBlankOrNull(this.gameVersion) ? null : this.gameVersion;
    }

    public String getSearch () {

        if (GenericValidator.isBlankOrNull(this.search)) {
            return "";
        }

        return this.search;
    }
}
