package com.diluv.api.graphql;

public class Author {

    private User user;
    private String role;

    public Author (User user, String role) {

        this.user = user;
        this.role = role;
    }
}
