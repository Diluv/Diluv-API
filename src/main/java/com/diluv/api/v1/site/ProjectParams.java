package com.diluv.api.v1.site;

import javax.ws.rs.QueryParam;

import org.apache.commons.validator.GenericValidator;

import com.diluv.confluencia.database.sort.ProjectSort;

public class ProjectParams {

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

    public ProjectSort getSort (ProjectSort defaultSort) {

        for (ProjectSort b : ProjectSort.values()) {
            if (b.name().equalsIgnoreCase(this.sort)) {
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
