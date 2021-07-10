package com.diluv.api.data;

import java.util.List;

import com.google.gson.annotations.Expose;

public class DataNotificationList {

    @Expose
    private final List<DataNotification> notifications;

    @Expose
    private final long totalNotifications;

    public DataNotificationList (List<DataNotification> notifications, long totalNotifications) {

        this.notifications = notifications;
        this.totalNotifications = totalNotifications;
    }
}
