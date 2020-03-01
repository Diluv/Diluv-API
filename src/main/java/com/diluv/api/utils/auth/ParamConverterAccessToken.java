package com.diluv.api.utils.auth;

import javax.ws.rs.ext.ParamConverter;

import com.diluv.api.utils.auth.tokens.AccessToken;
import com.diluv.api.utils.auth.tokens.APIAccessToken;
import com.diluv.api.utils.auth.tokens.Token;

public class ParamConverterAccessToken implements ParamConverter<Token> {

    public static final ParamConverter INSTANCE = new ParamConverterAccessToken();

    private ParamConverterAccessToken () {

    }

    @Override
    public Token fromString (String param) {

        AccessToken token = AccessToken.getToken(param);
        if (token != null) return token;
        return APIAccessToken.getToken(param);
    }

    @Override
    public String toString (Token token) {

        return token.toString();
    }
}