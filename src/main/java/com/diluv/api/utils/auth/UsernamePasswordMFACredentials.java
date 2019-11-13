package com.diluv.api.utils.auth;

import org.pac4j.core.credentials.UsernamePasswordCredentials;

public class UsernamePasswordMFACredentials extends UsernamePasswordCredentials {
    public UsernamePasswordMFACredentials (String username, String password) {

        super(username, password);
    }
}
