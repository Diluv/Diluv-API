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

public class AccessToken {
    private final long userId;
    private final String username;

    public AccessToken (long userId, String username) {

        this.userId = userId;
        this.username = username;
    }

    public static AccessToken getToken (String token) {

        try {
            SignedJWT jwt = JWTUtil.getJWT(token);
            if (jwt == null)
                return null;
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            if (!claims.getSubject().equalsIgnoreCase("accessToken")) {
                return null;
            }
            String audienceId = claims.getAudience().get(0);
            if (!GenericValidator.isLong(audienceId)) {
                return null;
            }
            long userId = Long.parseLong(audienceId);
            String username = claims.getStringClaim("username");
            return new AccessToken(userId, username);
        }
        catch (ParseException e) {
        	
        	DiluvAPI.LOGGER.warn("Failed to parse access token.", e);
        }
        return null;
    }

    public String generate (Date time) throws JOSEException {

        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
            .issuer("Diluv")
            .audience(String.valueOf(this.userId))
            .subject("accessToken")
            .expirationTime(time)
            .issueTime(new Date())
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
}
