package com.diluv.api.utils.auth.tokens;

import java.text.ParseException;
import java.util.Date;

import com.diluv.api.DiluvAPIServer;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

public class AccessToken extends Token {

    private static final String SUBJECT = "accessToken";

    public AccessToken (long userId, String username) {

        super(userId, username);
    }

    public static AccessToken getToken (String token) {

        JWTClaimsSet claims = Token.getToken(token, SUBJECT);

        if (claims != null) {

            try {
                final String audienceId = claims.getAudience().get(0);
                final long userId = Long.parseLong(audienceId);
                final String username = claims.getStringClaim("username");
                return new AccessToken(userId, username);
            }
            catch (final ParseException e) {

                DiluvAPIServer.LOGGER.warn("Failed to parse access token.", e);
            }
        }

        return null;
    }

    public String generate (Date time) throws JOSEException {

        return this.generate(time, SUBJECT);
    }
}