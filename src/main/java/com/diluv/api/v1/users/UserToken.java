package com.diluv.api.v1.users;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.gson.annotations.Expose;

public class UserToken {

    @NotBlank(message = "TOKEN_INVALID_NAME")
    @Size(max = 50, message = "TOKEN_INVALID_NAME")
    @Expose
    public String name;

    @NotNull(message = "TOKEN_INVALID_PERMISSIONS")
    @NotEmpty(message = "TOKEN_INVALID_PERMISSIONS")
    @Expose
    public List<String> permissions;

    public String getName () {

        return this.name.trim();
    }

    public List<String> getPermissions () {

        return this.permissions;
    }
}
