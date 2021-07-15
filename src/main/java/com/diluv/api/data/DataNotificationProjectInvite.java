package com.diluv.api.data;

import com.diluv.confluencia.database.record.NotificationProjectInvitesEntity;
import com.google.gson.annotations.Expose;

public class DataNotificationProjectInvite extends DataNotification {

    @Expose
    private final String status;

    @Expose
    private final DataUser sender;

    @Expose
    private final long projectId;

    @Expose
    private final String updatedAt;

    public DataNotificationProjectInvite (NotificationProjectInvitesEntity rs) {

        super(rs);
        this.status = rs.getStatus().name();
        this.sender = new DataUser(rs.getSender());
        this.projectId = rs.getProject().getId();
        this.updatedAt = rs.getUpdatedAt().toString();
    }
}
