package com.diluv.api.v1.notifications;

import com.diluv.confluencia.database.record.NotificationProjectInvitesStatus;
import com.google.gson.annotations.Expose;

import javax.validation.constraints.NotNull;

public class ProjectInvite {

    @NotNull(message = "INVALID_PROJECT_INVITE_STATUS")
    @Expose
    public NotificationProjectInvitesStatus status;
}
