package com.diluv.api.utils.auth;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.validator.GenericValidator;

import com.diluv.api.DiluvAPI;
import com.diluv.api.utils.Constants;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class RefreshToken {

    private final long userId;
    private final String username;
    private final String code;

    public RefreshToken (long userId, String username, String code) {

        this.userId = userId;
        this.username = username;
        this.code = code;
    }

    public static RefreshToken getToken (String token) {

        try {
            SignedJWT jwt = JWTUtil.getJWT(token);
            if (jwt == null)
                return null;
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            if (!claims.getSubject().equalsIgnoreCase("refreshToken")) {
                return null;
            }
            String audienceId = claims.getAudience().get(0);
            if (!GenericValidator.isLong(audienceId)) {
                return null;
            }
            long userId = Long.parseLong(audienceId);
            String username = claims.getStringClaim("username");
            String code = claims.getStringClaim("code");
            if (username == null || code == null) {
                return null;
            }
            return new RefreshToken(userId, username, code);
        }
        catch (ParseException e) {
            DiluvAPI.LOGGER.throwing(RefreshToken.class.getName(), "getToken (String token)", e);
        }
        return null;
    }

    public String generate (Date time) throws JOSEException {

        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
            .issuer("Diluv")
            .audience(String.valueOf(this.userId))
            .subject("refreshToken")
            .expirationTime(time)
            .issueTime(new Date())
            .claim("code", this.code)
            .claim("username", this.username);

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

    public String getCode () {

        return this.code;
    }
}
