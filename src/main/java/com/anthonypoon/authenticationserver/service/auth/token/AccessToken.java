package com.anthonypoon.authenticationserver.service.auth.token;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccessToken extends AuthToken {
    private final String identifier;

    @Override
    public Type getType() {
        return Type.ACCESS;
    }
}
