package com.diluv.api.utils.permissions;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.UserRolesEntity;
import com.diluv.confluencia.database.record.UsersEntity;

public enum UserPermissions {

    VIEW_ADMIN("admin.view");

    private static final List<UserPermissions> ADMIN = Arrays.asList(VIEW_ADMIN);
    private final String name;

    UserPermissions (String name) {

        this.name = name;
    }

    public String getName () {

        return this.name;
    }

    public static boolean hasPermission (@Nullable Token token, UserPermissions permissions) {

        if (token == null)
            return false;

        UsersEntity user = Confluencia.USER.findOneByUserId(token.getUserId());
        if (user == null) {
            return false;
        }
        UserRolesEntity userRole = user.getUserRole();
        if (userRole == null) {
            return false;
        }
        if ("admin".equals(userRole.getRole().getName())) {
            if (ADMIN.contains(permissions)) {
                return true;
            }
        }

        return false;
    }
}

