//package com.diluv.api.utils.auth.tokens;
//
//import java.text.ParseException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.Nullable;
//
//import com.diluv.api.DiluvAPIServer;
//import com.diluv.confluencia.database.record.APITokenRecord;
//import com.nimbusds.jose.JOSEException;
//import com.nimbusds.jwt.JWTClaimsSet;
//
//import static com.diluv.api.Main.DATABASE;
//
//public class APIAccessToken extends Token {
//
//    private static final String SUBJECT = "apiToken";
//
//    private final String code;
//    private final List<String> permissions;
//
//    public APIAccessToken (long userId, String username, String code, List<String> permissions) {
//
//        super(userId, username);
//        this.code = code;
//        this.permissions = permissions;
//    }
//
//    public static APIAccessToken getToken (@Nullable String token) {
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
//                final List<String> permissions = claims.getStringListClaim("permissions");
//
//                APITokenRecord apiToken = DATABASE.userDAO.findAPITokenByUserIdAndCode(userId, code);
//                if (apiToken == null) {
//                    return null;
//                }
//                return new APIAccessToken(userId, username, code, permissions);
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
//    public String generate () throws JOSEException {
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("code", this.code);
//        data.put("permissions", this.permissions);
//        return this.generate(null, SUBJECT, data);
//    }
//
//    public String getCode () {
//
//        return this.code;
//    }
//
//    public List<String> getPermissions () {
//
//        return this.permissions;
//    }
//}
