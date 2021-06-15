package com.diluv.api.graphql.data;

import com.diluv.confluencia.database.record.RegistrationCodesEntity;

public class RegistrationCodes {

    private String code;
    private long createdAt;

    private RegistrationCodesEntity entity;

    public RegistrationCodes (RegistrationCodesEntity entity) {

        this.code = entity.getCode();
        this.createdAt = entity.getCreatedAt().getTime();
        this.entity = entity;
    }

    public RegistrationCodesEntity getEntity () {

        return this.entity;
    }
}
