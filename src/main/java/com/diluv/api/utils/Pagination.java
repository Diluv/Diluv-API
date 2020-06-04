package com.diluv.api.utils;

@Deprecated
public class Pagination {
    public static int getLimit (Integer queryLimit) {

        if (queryLimit == null) {
            return 20;
        }

        if (queryLimit <= 20) {
            return 20;
        }

        if (queryLimit >= 100) {
            return 100;
        }

        return queryLimit;
    }

    public static long getPage (Long page) {

        if (page == null || page < 1) {
            return 1;
        }

        return page;
    }
}
