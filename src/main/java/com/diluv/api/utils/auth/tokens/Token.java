package com.diluv.api.utils.auth.tokens;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.annotation.Nullable;

import com.diluv.api.DiluvAPIServer;
import com.diluv.api.utils.Constants;
import com.diluv.api.utils.auth.JWTUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class Token {
    private final long userId;
    private final String username;

    protected Token (long userId, String username) {

        this.userId = userId;
        this.username = username;
    }

    protected static JWTClaimsSet getToken (@Nullable String token, String subject) {

        if (token != null) {

            try {
                final SignedJWT jwt = JWTUtil.getJWT(token);
                if (jwt == null) {
                    return null;
                }
                final JWTClaimsSet claims = jwt.getJWTClaimsSet();
                if (!claims.getSubject().equalsIgnoreCase(subject)) {
                    return null;
                }
                return claims;
            }
            catch (final ParseException e) {

                DiluvAPIServer.LOGGER.warn("Failed to parse access token.", e);
            }
        }

        return null;
    }

    protected String generate (Date time, String subject) throws JOSEException {

        return this.generate(time, subject, Collections.emptyMap());
    }

    protected String generate (Date time, String subject, Map<String, Object> claims) throws JOSEException {

        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        builder.issuer("Diluv").audience(String.valueOf(this.userId)).subject(subject).expirationTime(time).issueTime(new Date());
        builder.claim("username", this.username);
        claims.forEach(builder::claim);

        final JWSSigner signer = new RSASSASigner(Constants.PRIVATE_KEY);
        final SignedJWT accessToken = new SignedJWT(new JWSHeader(JWSAlgorithm.RS512), builder.build());
        accessToken.sign(signer);
        return accessToken.serialize();
    }

    public long getUserId () {

        return this.userId;
    }

    public String getUsername () {

        return this.username;
    }
}
