package com.anthonypoon.authenticationserver.service.auth.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@Getter
@SuperBuilder
public abstract class AuthToken {
    @Setter
    private String tokenValue;
    @Setter
    private ZonedDateTime expireAt;
    public abstract Type getType();
    public enum Type {
        ACCESS,
        REFRESH,
        TWO_FA_CHALLENGE,
        REAUTHENTICATE,
    }
}
