package com.diluv.api.utils.auth;

import javax.ws.rs.ext.ParamConverter;

public class ParamConverterAccessToken implements ParamConverter<AccessToken> {

    public static final ParamConverter INSTANCE = new ParamConverterAccessToken();
    
    private ParamConverterAccessToken() {
        
    }
    
    @Override
    public AccessToken fromString (String param) {
        
        return AccessToken.getToken(param);
    }

    @Override
    public String toString (AccessToken token) {
        
        return token.toString();
    }
}