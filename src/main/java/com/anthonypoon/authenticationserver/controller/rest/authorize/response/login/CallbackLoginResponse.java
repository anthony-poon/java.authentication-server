package com.anthonypoon.authenticationserver.controller.rest.authorize.response.login;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CallbackLoginResponse extends LoginResponse {
    private String callback;

    @Override
    public Type getType() {
        return Type.CALLBACK;
    }
}
