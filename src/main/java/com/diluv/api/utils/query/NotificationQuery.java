package com.diluv.api.utils.query;

import javax.ws.rs.QueryParam;

import com.diluv.confluencia.database.record.NotificationType;
import com.diluv.confluencia.database.sort.NotificationSort;
import com.diluv.confluencia.database.sort.Sort;

public class NotificationQuery extends PaginationQuery {

    @QueryParam("type")
    private NotificationType type;

    @QueryParam("isRead")
    private Boolean isRead;

    @Override
    public Sort getSort (Sort defaultSort) {

        for (Sort b : NotificationSort.LIST) {
            if (b.getSlug().equalsIgnoreCase(this.sort)) {
                return b;
            }
        }
        return defaultSort;
    }

    public NotificationType getType () {

        return this.type;
    }

    public Boolean getRead () {

        return this.isRead;
    }
}
