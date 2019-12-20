package com.diluv.api.utils.auth;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.validator.GenericValidator;

import com.diluv.api.utils.Constants;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import static com.diluv.api.utils.RequestUtil.BEARER;

public class JWTUtil {

    public static String generateAccessToken (long id, String username, Date time) throws JOSEException {

        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
            .issuer("Diluv")
            .audience(String.valueOf(id))
            .subject("accessToken")
            .expirationTime(time)
            .issueTime(new Date())
            .claim("username", username);

        final JWSSigner signer = new RSASSASigner(Constants.PRIVATE_KEY);
        final SignedJWT accessToken = new SignedJWT(new JWSHeader(JWSAlgorithm.RS512), builder.build());
        accessToken.sign(signer);
        return accessToken.serialize();
    }

    public static String generateRefreshToken (long id, Date time, String randomKey) throws JOSEException {

        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
            .issuer("Diluv")
            .audience(String.valueOf(id))
            .subject("refreshToken")
            .expirationTime(time)
            .issueTime(new Date())
            .claim("key", randomKey);

        final JWSSigner signer = new RSASSASigner(Constants.PRIVATE_KEY);
        final SignedJWT accessToken = new SignedJWT(new JWSHeader(JWSAlgorithm.RS512), builder.build());
        accessToken.sign(signer);
        return accessToken.serialize();
    }

    public static SignedJWT getJWT (String token) {

        try {
            SignedJWT jwt = SignedJWT.parse(token.substring(BEARER.length()));

            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            if (!claims.getSubject().equalsIgnoreCase("accessToken"))
                return null;

            if (claims.getStringClaim("username") == null) {
                return null;
            }
            return jwt;
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUsername (SignedJWT jwt) {

        try {
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            if (!claims.getSubject().equalsIgnoreCase("accessToken"))
                return null;

            return claims.getStringClaim("username");
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Long getUserId (SignedJWT jwt) {

        try {
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            if (claims.getAudience().isEmpty())
                return null;

            String id = claims.getAudience().get(0);
            if (GenericValidator.isLong(id)) {
                return Long.valueOf(id);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
