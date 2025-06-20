package com.anthonypoon.authenticationserver.service.token.factory;

import com.anthonypoon.authenticationserver.service.token.exception.TokenDecodeException;
import com.anthonypoon.authenticationserver.service.token.exception.TokenEncodeException;
import com.anthonypoon.authenticationserver.service.token.token.Token;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

public abstract class TokenFactory<T extends Token, C> {
    public abstract Class<? extends Token> getType();
    public abstract String sign(C context) throws TokenEncodeException;
    public abstract T decode(String tokenValue) throws TokenDecodeException;

    public static Instant getExpiration(Map<String, Object> claims) throws TokenDecodeException {
        if (!claims.containsKey("exp")) {
            throw new TokenDecodeException("Missing expiration from claims");
        }
        Object expObj = claims.get("exp");
        if (expObj instanceof Date) {
            return ((Date) expObj).toInstant();
        } else if (expObj instanceof Number) {
            return Instant.ofEpochSecond(((Number) expObj).longValue());
        }
        throw new TokenDecodeException("Invalid exp claim");
    }
}
