package com.anthonypoon.authenticationserver.controller.rest.authorize.response.login;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class RequireTwoFALoginResponse extends LoginResponse {
    private String challenge;
    @Override
    public Type getType() {
        return Type.REQUIRE_TWO_FA;
    }
}
