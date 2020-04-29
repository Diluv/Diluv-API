package com.diluv.api.utils.auth;

import java.text.ParseException;
import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;

import com.diluv.api.utils.Constants;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.confluencia.database.record.ReferenceTokenRecord;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

import static com.diluv.api.Main.DATABASE;

public class JWTUtil {

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

        if (rawToken != null) {
            if (JWTUtil.isBearerToken(rawToken)) {

                String token = rawToken.substring(JWTUtil.BEARER.length());
                JWT jwt = getJWT(token);

                if (jwt != null) {
                    try {
                        ConfigurableJWTProcessor<SecurityContext> processor = Constants.JWT_PROCESSOR;
                        if (processor == null)
                            return null;

                        JWTClaimsSet claimsSet = processor.process(jwt, null);
                        if (claimsSet != null) {
                            long userId = Long.parseLong(claimsSet.getSubject());
                            return new Token(userId, ProjectPermissions.getAllPermissions());
                        }
                    }
                    catch (JOSEException | BadJOSEException | NumberFormatException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                byte[] sha256 = DigestUtils.sha256(token + ":reference_token");
                String key = Base64.getEncoder().encodeToString(sha256);
                ReferenceTokenRecord record = DATABASE.securityDAO.findPersistedGrantByKeyAndType(key, "reference_token");
                if (record == null)
                    return null;

                if (System.currentTimeMillis() - record.getExpiration() > 0) {
                    return null;
                }

                //TODO Implement a table
                long userId = Long.parseLong(record.getSubjectId());
                return new Token(userId, ProjectPermissions.getAllPermissions());
            }
        }

        return null;
    }

    public static boolean isBearerToken (String token) {

        return BEARER.regionMatches(true, 0, token, 0, BEARER.length());
    }
}
