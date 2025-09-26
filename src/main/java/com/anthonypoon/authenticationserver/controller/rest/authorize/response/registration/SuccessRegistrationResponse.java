package com.anthonypoon.authenticationserver.controller.rest.authorize.response.registration;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SuccessRegistrationResponse extends RegistrationResponse {
    private String refresh;
    @Override
    public Type getType() {
        return Type.SUCCESS;
    }
}
