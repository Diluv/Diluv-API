package com.diluv.api.utils.query;

import javax.ws.rs.QueryParam;

import com.diluv.confluencia.database.sort.Sort;

public abstract class PaginationQuery {

    @QueryParam("page")
    private Long page;

    @QueryParam("limit")
    private Integer limit;

    @QueryParam("sort")
    protected String sort;

    public long getPage () {

        if (this.page == null || this.page < 1) {
            return 1;
        }

        return this.page;
    }

    public int getLimit () {

        if (this.limit == null) {
            return 20;
        }

        if (this.limit <= 20) {
            return 20;
        }

        if (this.limit >= 100) {
            return 100;
        }

        return this.limit;
    }

    public abstract Sort getSort (Sort defaultSort);
}
