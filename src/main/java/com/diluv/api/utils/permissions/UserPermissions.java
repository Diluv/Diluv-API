package com.diluv.api.utils.permissions;

public enum UserPermissions {

    VIEW_ADMIN("admin.view");

    private final String name;

    UserPermissions (String name) {

        this.name = name;
    }

    public String getName () {

        return this.name;
    }
}

