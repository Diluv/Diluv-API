package com.diluv.api.utils.query;

import com.diluv.confluencia.database.sort.GameSort;
import com.diluv.confluencia.database.sort.Sort;

public class GameQuery extends PaginationQuery {

    @Override
    public Sort getSort (Sort defaultSort) {

        for (Sort b : GameSort.LIST) {
            if (b.getSlug().equalsIgnoreCase(this.sort)) {
                return b;
            }
        }
        return defaultSort;
    }
}
