package com.anthonypoon.authenticationserver.controller.rest.authorize.response;

import com.anthonypoon.authenticationserver.domains.role.UserRole;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@SuperBuilder
public class SuccessTokenResponse extends GetTokenResponse {
    private String identity;
    private Set<UserRole> roles;
    private String access;
    private String refresh;

    @Override
    public Type getType() {
        return Type.SUCCESS;
    }
}
