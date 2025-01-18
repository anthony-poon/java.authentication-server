package com.anthonypoon.authenticationserver.controller.rest.authorize.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CallbackTokenResponse extends GetTokenResponse {
    private String callback;

    @Override
    public Type getType() {
        return Type.CALLBACK;
    }
}
