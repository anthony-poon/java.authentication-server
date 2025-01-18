package com.anthonypoon.authenticationserver.controller.rest.authorize.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class RequireTwoFAResponse extends GetTokenResponse {
    private String token;
    @Override
    public Type getType() {
        return Type.REQUIRE_TWO_FA;
    }
}
