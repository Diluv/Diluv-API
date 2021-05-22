package com.diluv.api.utils.auth;

import com.diluv.api.DiluvAPIServer;
import com.diluv.api.utils.Constants;
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

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.util.encoders.Hex;

import javax.ws.rs.WebApplicationException;

import java.text.ParseException;
import java.util.UUID;

public class JWTUtil {

    public static final String BEARER = "Bearer ";

    protected static UUID getUUID (String token) {

        try {
            return UUID.fromString(token);
        }
        catch (IllegalArgumentException ignored) {
        }
        return null;
    }

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
            throw new WebApplicationException(ErrorMessage.USER_INVALID_TOKEN.respond());
        }

        String token = rawToken.substring(JWTUtil.BEARER.length());

        UUID uuid = getUUID(token);
        if (uuid != null) {
            return Confluencia.getTransaction(session -> {
                APITokensEntity record = Confluencia.SECURITY.findAPITokensByToken(session, getSha512UUID(uuid));
                if (record == null) {
                    throw new WebApplicationException(ErrorMessage.USER_INVALID_TOKEN.respond("API token not found"));
                }
                long userId = record.getUser().getId();
                return new Token(userId, true, ProjectPermissions.getAllPermissions());
            });
        }

        JWT jwt = getJWT(token);

        if (jwt != null) {
            ConfigurableJWTProcessor<SecurityContext> processor = Constants.JWT_PROCESSOR;
            if (processor == null) {
                DiluvAPIServer.LOGGER.error("Processor is null.");
                throw new WebApplicationException(ErrorMessage.THROWABLE.respond());
            }
            try {
                JWTClaimsSet claimsSet = processor.process(jwt, null);
                if (claimsSet != null) {
                    return new Token(claimsSet.getLongClaim("userId"), false, ProjectPermissions.getAllPermissions());
                }
            }
            catch (JOSEException | BadJOSEException | NumberFormatException | ParseException e) {
                DiluvAPIServer.LOGGER.catching(e);
                throw new WebApplicationException(ErrorMessage.USER_INVALID_TOKEN.respond("Invalid token format"));
            }
        }

        throw new WebApplicationException(ErrorMessage.USER_INVALID_TOKEN.respond());
    }

    public static boolean isBearerToken (String token) {

        return BEARER.regionMatches(true, 0, token, 0, BEARER.length());
    }

    public static String getSha512UUID (UUID uuid) {

        byte[] sha512 = DigestUtils.sha512(uuid.toString());
        return Hex.toHexString(sha512);
    }
}
