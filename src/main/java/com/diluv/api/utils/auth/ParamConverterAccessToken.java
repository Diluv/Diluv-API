package com.diluv.api.utils.auth;

import javax.ws.rs.ext.ParamConverter;

import com.diluv.api.utils.auth.tokens.Token;

public class ParamConverterAccessToken implements ParamConverter<Token> {

    public static final ParamConverter<Token> INSTANCE = new ParamConverterAccessToken();

    @Override
    public Token fromString (String param) {

        return JWTUtil.getToken(param);
    }

    @Override
    public String toString (Token token) {

        return token.toString();
    }
}