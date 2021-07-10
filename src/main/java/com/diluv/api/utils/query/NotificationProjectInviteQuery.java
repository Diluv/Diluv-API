package com.diluv.api.utils.query;

import javax.ws.rs.QueryParam;

import com.diluv.confluencia.database.record.NotificationProjectInvitesStatus;
import com.diluv.confluencia.database.sort.NotificationSort;
import com.diluv.confluencia.database.sort.Sort;

public class NotificationProjectInviteQuery extends PaginationQuery {

    @QueryParam("status")
    private NotificationProjectInvitesStatus status;

    @Override
    public Sort getSort (Sort defaultSort) {

        for (Sort b : NotificationSort.LIST) {
            if (b.getSlug().equalsIgnoreCase(this.sort)) {
                return b;
            }
        }
        return defaultSort;
    }

    public NotificationProjectInvitesStatus getStatus () {

        return this.status;
    }
}
