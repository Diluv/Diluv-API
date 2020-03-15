package com.diluv.api.utils.auth.tokens;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.diluv.api.DiluvAPIServer;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

public class AccessToken extends Token {

    private static final String SUBJECT = "accessToken";
    private final List<String> roles;

    public AccessToken (long userId, String username, List<String> roles) {

        super(userId, username);
        this.roles = roles;
    }

    public static AccessToken getToken (String token) {

        JWTClaimsSet claims = Token.getToken(token, SUBJECT);

        if (claims != null) {

            try {
                final String audienceId = claims.getAudience().get(0);
                final long userId = Long.parseLong(audienceId);
                final String username = claims.getStringClaim("username");
                final List<String> roles = claims.getStringListClaim("roles");
                return new AccessToken(userId, username, roles);
            }
            catch (final ParseException e) {

                DiluvAPIServer.LOGGER.warn("Failed to parse access token.", e);
            }
        }

        return null;
    }

    public String generate (Date time) throws JOSEException {

        Map<String, Object> data = Collections.singletonMap("roles", this.roles);
        return this.generate(time, SUBJECT, data);
    }

    public List<String> getRoles () {

        return this.roles;
    }
}