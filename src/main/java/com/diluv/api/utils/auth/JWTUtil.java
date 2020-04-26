package com.diluv.api.utils.auth;

import java.text.ParseException;

import com.diluv.api.utils.Constants;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

public class JWTUtil {

    public static final String BEARER = "Bearer ";

    public static JWTClaimsSet getJWT (String token) {

        try {

            if (JWTUtil.isBearerToken(token)) {

                return Constants.JWT_PROCESSOR.process(token.substring(JWTUtil.BEARER.length()), null);
            }
        }
        catch (ParseException | JOSEException | BadJOSEException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isBearerToken (String token) {

        return BEARER.regionMatches(true, 0, token, 0, BEARER.length());
    }
}
