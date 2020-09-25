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

        return getPage(this.page);
    }

    public int getLimit(){
        return getLimit(this.limit);
    }

    public static long getPage (Long page) {

        if (page == null || page < 1) {
            return 1;
        }

        return page;
    }

    public static int getLimit (Integer limit) {

        if (limit == null) {
            return 20;
        }

        if (limit <= 10) {
            return 10;
        }

        if (limit >= 100) {
            return 100;
        }

        return limit;
    }

    public abstract Sort getSort (Sort defaultSort);
}
