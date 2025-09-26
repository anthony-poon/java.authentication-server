package com.anthonypoon.authenticationserver.controller.rest.authorize.response.registration;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class RegistrationResponse {
    private Type type;

    enum Type {
        REQUIRE_VALIDATION,
        SUCCESS
    }

    public abstract Type getType();
}
