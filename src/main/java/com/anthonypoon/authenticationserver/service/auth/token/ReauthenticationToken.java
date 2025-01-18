package com.anthonypoon.authenticationserver.service.auth.token;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ReauthenticationToken extends AuthToken {
    private final String identifier;

    @Override
    public Type getType() {
        return Type.REAUTHENTICATE;
    }
}
