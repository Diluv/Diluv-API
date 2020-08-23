package com.diluv.api.utils.auth.tokens;

import java.util.Collections;

public class InvalidToken extends Token {
    public InvalidToken () {

        super(-1, Collections.emptyList());
    }
}
