package com.diluv.api.graphql.data;

import com.diluv.confluencia.database.record.RegistrationCodesEntity;

public class RegistrationCodes {

    private String code;
    private String createdAt;

    private RegistrationCodesEntity entity;

    public RegistrationCodes (RegistrationCodesEntity entity) {

        this.code = entity.getCode();
        this.createdAt = entity.getCreatedAt().toString();
        this.entity = entity;
    }

    public RegistrationCodesEntity getEntity () {

        return this.entity;
    }
}
