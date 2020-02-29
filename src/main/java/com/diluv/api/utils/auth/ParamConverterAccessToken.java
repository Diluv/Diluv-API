package com.diluv.api.utils.auth;

import javax.ws.rs.ext.ParamConverter;

import com.diluv.api.utils.auth.tokens.AccessToken;
import com.diluv.api.utils.auth.tokens.LongLivedAccessToken;
import com.diluv.api.utils.auth.tokens.Token;

public class ParamConverterAccessToken implements ParamConverter<Token> {

    public static final ParamConverter INSTANCE = new ParamConverterAccessToken();

    private ParamConverterAccessToken () {

    }

    @Override
    public Token fromString (String param) {

        AccessToken token = AccessToken.getToken(param);
        if (token != null) return token;
        LongLivedAccessToken test = LongLivedAccessToken.getToken(param);
        return test;
    }

    @Override
    public String toString (Token token) {

        return token.toString();
    }
}