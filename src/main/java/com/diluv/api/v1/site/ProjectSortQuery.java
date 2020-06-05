package com.diluv.api.v1.site;

import javax.ws.rs.QueryParam;

import org.apache.commons.validator.GenericValidator;

import com.diluv.confluencia.database.sort.ProjectSort;
import com.diluv.confluencia.database.sort.Sort;

public class ProjectSortQuery {

    @QueryParam("page")
    private Long page;

    @QueryParam("limit")
    private Integer limit;

    @QueryParam("sort")
    public String sort;

    @QueryParam("version")
    public String version;

    @QueryParam("search")
    private String search;

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
