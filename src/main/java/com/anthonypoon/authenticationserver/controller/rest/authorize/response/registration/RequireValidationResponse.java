package com.anthonypoon.authenticationserver.controller.rest.authorize.response.registration;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class RequireValidationResponse extends RegistrationResponse {
    @Override
    public Type getType() {
        return Type.REQUIRE_VALIDATION;
    }
}
