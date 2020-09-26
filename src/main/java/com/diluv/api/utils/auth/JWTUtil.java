package com.diluv.api.utils.auth;

import java.text.ParseException;
import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;

import com.diluv.api.DiluvAPIServer;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.auth.tokens.InvalidToken;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.PersistedGrantsEntity;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

public class JWTUtil {

    public static final InvalidToken INVALID = new InvalidToken();
    public static final String BEARER = "Bearer ";

    protected static JWT getJWT (String token) {

        try {
            return JWTParser.parse(token);
        }
        catch (ParseException ignored) {
        }
        return null;
    }

    public static Token getToken (String rawToken) {

        if (rawToken == null) {
            return null;
        }

        if (!JWTUtil.isBearerToken(rawToken)) {
            return INVALID;
        }

        String token = rawToken.substring(JWTUtil.BEARER.length());
        JWT jwt = getJWT(token);

        if (jwt != null) {
            try {
                ConfigurableJWTProcessor<SecurityContext> processor = Constants.JWT_PROCESSOR;
                if (processor == null)
                    return INVALID;

                JWTClaimsSet claimsSet = processor.process(jwt, null);
                if (claimsSet != null) {
                    long userId = Long.parseLong(claimsSet.getSubject());
                    return new Token(userId, false, ProjectPermissions.getAllPermissions());
                }
            }
            catch (JOSEException | BadJOSEException | NumberFormatException e) {
                DiluvAPIServer.LOGGER.catching(e);
                return INVALID;
            }
        }

        String type = "reference_token";
        byte[] sha256 = DigestUtils.sha256(token + ":" + type);
        String key = Base64.getEncoder().encodeToString(sha256);
        PersistedGrantsEntity record = Confluencia.SECURITY.findPersistedGrantByKeyAndType(key, type);
        if (record == null) {
            return INVALID;
        }

        long currentTime = System.currentTimeMillis();

        if (currentTime < record.getCreationTime().getTime()) {
            return INVALID;
        }
        if (currentTime > record.getExpiration().getTime()) {
            return INVALID;
        }

        //TODO Implement a table
        long userId = Long.parseLong(record.getSubjectId());
        return new Token(userId, true, ProjectPermissions.getAllPermissions());
    }

    public static boolean isBearerToken (String token) {

        return BEARER.regionMatches(true, 0, token, 0, BEARER.length());
    }
}
