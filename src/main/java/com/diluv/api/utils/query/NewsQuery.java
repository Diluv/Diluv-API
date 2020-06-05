package com.diluv.api.utils.query;

import com.diluv.confluencia.database.sort.NewsSort;
import com.diluv.confluencia.database.sort.Sort;

public class NewsQuery extends PaginationQuery {
    @Override
    public Sort getSort (Sort defaultSort) {

        for (Sort b : NewsSort.LIST) {
            if (b.getSlug().equalsIgnoreCase(this.sort)) {
                return b;
            }
        }
        return defaultSort;
    }
}
