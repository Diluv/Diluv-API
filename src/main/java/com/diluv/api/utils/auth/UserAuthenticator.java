package com.diluv.api.utils.auth;

import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import com.diluv.api.database.dao.UserDAO;
import com.diluv.api.database.record.UserRecord;

public class UserAuthenticator implements Authenticator<UsernamePasswordMFACredentials> {
    private final UserDAO userDAO;

    public UserAuthenticator (UserDAO userDAO) {

        this.userDAO = userDAO;
    }

    @Override
    public void validate (UsernamePasswordMFACredentials credentials, WebContext context) {

        if (credentials == null) {
            throw new CredentialsException("No credential");
        }
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        if (CommonHelper.isBlank(username)) {
            throw new CredentialsException("Username cannot be blank");
        }
        if (CommonHelper.isBlank(password)) {
            throw new CredentialsException("Password cannot be blank");
        }

        UserRecord userRecord = this.userDAO.findOneByUsername(username);
        if (userRecord == null) {
            throw new CredentialsException("Username not found?");
        }

        if (!userRecord.getPasswordType().equalsIgnoreCase("bcrypt")) {
            throw new CredentialsException("Invalid password type");
        }

        if (!OpenBSDBCrypt.checkPassword(userRecord.getPassword(), password.toCharArray())) {
            throw new CredentialsException("Invalid password type");

        }
        final CommonProfile profile = new CommonProfile();
        profile.setId(username);
        profile.addAttribute(Pac4jConstants.USERNAME, username);
        credentials.setUserProfile(profile);
    }
}
