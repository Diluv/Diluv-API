package com.diluv.api.utils.auth;

import com.diluv.api.utils.auth.tokens.Token;

import javax.ws.rs.ext.ParamConverter;

public class ParamConverterAccessToken implements ParamConverter<Token> {

    public static final ParamConverter INSTANCE = new ParamConverterAccessToken();

    private ParamConverterAccessToken () {

    }

    @Override
    public Token fromString (String param) {

        return JWTUtil.getToken(param);
    }

    @Override
    public String toString (Token token) {

        return token.toString();
    }
}