package com.diluv.api.utils.auth;

import java.text.ParseException;
import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;

import com.diluv.api.DiluvAPIServer;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.auth.tokens.ErrorToken;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.permissions.ProjectPermissions;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.APITokensEntity;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

import org.bouncycastle.util.encoders.Hex;

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

        if (rawToken == null) {
            return null;
        }

        if (!JWTUtil.isBearerToken(rawToken)) {
            return new ErrorToken(ErrorMessage.USER_INVALID_TOKEN.respond());
        }

        String token = rawToken.substring(JWTUtil.BEARER.length());
        JWT jwt = getJWT(token);

        if (jwt != null) {
            try {
                ConfigurableJWTProcessor<SecurityContext> processor = Constants.JWT_PROCESSOR;
                if (processor == null) {
                    return new ErrorToken(ErrorMessage.THROWABLE.respond());
                }

                JWTClaimsSet claimsSet = processor.process(jwt, null);
                if (claimsSet != null) {
                    long userId = Long.parseLong(claimsSet.getSubject());
                    return new Token(userId, false, ProjectPermissions.getAllPermissions());
                }
            }
            catch (JOSEException | BadJOSEException | NumberFormatException e) {
                DiluvAPIServer.LOGGER.catching(e);
                return new ErrorToken(ErrorMessage.USER_INVALID_TOKEN.respond("Invalid token format"));
            }
        }
        byte[] sha512 = DigestUtils.sha512(token);
        String apiToken = Hex.toHexString(sha512);

        return Confluencia.getTransaction(session -> {
            APITokensEntity record = Confluencia.SECURITY.findAPITokensByToken(session, apiToken);
            if (record == null) {
                return new ErrorToken(ErrorMessage.USER_INVALID_TOKEN.respond("API token not found"));
            }

            long userId = record.getUser().getId();
            return new Token(userId, true, ProjectPermissions.getAllPermissions());
        });
    }

    public static boolean isBearerToken (String token) {

        return BEARER.regionMatches(true, 0, token, 0, BEARER.length());
    }
}
