package com.anthonypoon.authenticationserver.service.auth.factory;

import com.anthonypoon.authenticationserver.service.auth.token.AuthToken;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public abstract class TokenFactory<T extends AuthToken> {
    public abstract Map<String, Object> dehydrate(T token);

    protected Map<String, Object> dehydrate(Map<String, Object> claims, T token) {
        claims.put("_type", token.getType());
        return claims;
    }
    public abstract T rehydrate(Map<String, Object> claims);

    protected T rehydrate(Map<String, Object> claims, T token) {
        token.setExpireAt(ZonedDateTime.ofInstant(
                Instant.ofEpochSecond((int) claims.get("exp")),
                ZoneId.systemDefault()
        ));
        return token;
    }
}
