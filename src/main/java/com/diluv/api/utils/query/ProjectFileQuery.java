package com.diluv.api.utils.query;

import javax.ws.rs.QueryParam;

import com.diluv.confluencia.database.sort.ProjectFileSort;
import com.diluv.confluencia.database.sort.Sort;

public class ProjectFileQuery extends PaginationQuery {

    @QueryParam("game_version")
    private String gameVersion;

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

        return this.gameVersion;
    }
}
