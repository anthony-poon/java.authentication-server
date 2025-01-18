package com.anthonypoon.authenticationserver.controller.rest.authorize.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class GetTokenResponse {
    public abstract Type getType();
    public enum Type {
        REQUIRE_TWO_FA,
        SUCCESS,
        CALLBACK,
        REAUTHENTICATED
    }
}
