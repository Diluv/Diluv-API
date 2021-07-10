package com.diluv.api.data;

import com.diluv.confluencia.database.record.NotificationsEntity;
import com.google.gson.annotations.Expose;

public class DataNotification {

    @Expose
    private final long id;

    @Expose
    private final String text;

    @Expose
    private final String type;

    @Expose
    private final String createdAt;

    public DataNotification (NotificationsEntity rs) {

        this.id = rs.getId();
        this.text = rs.getText();
        this.type = rs.getType().name();
        this.createdAt = rs.getCreatedAt().toString();
    }
}
