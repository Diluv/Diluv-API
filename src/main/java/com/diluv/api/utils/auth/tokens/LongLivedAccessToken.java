package com.diluv.api.utils.auth.tokens;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.diluv.api.DiluvAPIServer;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

public class LongLivedAccessToken extends Token {

    private static final String SUBJECT = "long_lived";
    private final List<String> permissions;

    public LongLivedAccessToken (long userId, String username, List<String> permissions) {

        super(userId, username);
        this.permissions = permissions;
    }

    public static LongLivedAccessToken getToken (@Nullable String token) {

        JWTClaimsSet claims = Token.getToken(token, SUBJECT);

        if (claims != null) {

            try {
                final String audienceId = claims.getAudience().get(0);
                final long userId = Long.parseLong(audienceId);
                final String username = claims.getStringClaim("username");
                final List<String> permissions = claims.getStringListClaim("permissions");
                return new LongLivedAccessToken(userId, username, permissions);
            }
            catch (final ParseException e) {

                DiluvAPIServer.LOGGER.warn("Failed to parse access token.", e);
            }
        }

        return null;
    }

    public String generate (Date time) throws JOSEException {

        Map<String, Object> data = Collections.singletonMap("permissions", this.permissions);

        return this.generate(time, SUBJECT, data);
    }

    public List<String> getPermissions () {

        return this.permissions;
    }
}
