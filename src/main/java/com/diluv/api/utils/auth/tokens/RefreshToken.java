//package com.diluv.api.utils.auth.tokens;
//
//import java.text.ParseException;
//import java.util.Collections;
//import java.util.Date;
//import java.util.Map;
//
//import com.diluv.api.DiluvAPIServer;
//import com.nimbusds.jose.JOSEException;
//import com.nimbusds.jwt.JWTClaimsSet;
//
//public class RefreshToken extends Token {
//
//    private static final String SUBJECT = "refreshToken";
//    private final String code;
//
//    public RefreshToken (long userId, String username, String code) {
//
//        super(userId, username);
//        this.code = code;
//    }
//
//    public static RefreshToken getToken (String token) {
//
//        JWTClaimsSet claims = Token.getToken(token, SUBJECT);
//
//        if (claims != null) {
//
//            try {
//                final String audienceId = claims.getAudience().get(0);
//                final long userId = Long.parseLong(audienceId);
//                final String username = claims.getStringClaim("username");
//                final String code = claims.getStringClaim("code");
//                return new RefreshToken(userId, username, code);
//            }
//            catch (final ParseException e) {
//
//                DiluvAPIServer.LOGGER.warn("Failed to parse access token.", e);
//            }
//        }
//
//        return null;
//    }
//
//    public String generate (Date time) throws JOSEException {
//
//        Map<String, Object> data = Collections.singletonMap("code", this.code);
//        return this.generate(time, SUBJECT, data);
//    }
//
//    public String getCode () {
//
//        return this.code;
//    }
//}
