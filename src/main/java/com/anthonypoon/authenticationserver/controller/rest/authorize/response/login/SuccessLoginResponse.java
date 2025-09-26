package com.anthonypoon.authenticationserver.controller.rest.authorize.response.login;

import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.domains.role.ApplicationRole;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Getter
@SuperBuilder
public class SuccessLoginResponse extends LoginResponse {
    private String identity;
    private Set<ApplicationRole> roles;
    private String access;
    private String refresh;

    @Override
    public Type getType() {
        return Type.SUCCESS;
    }

    public static SuccessLoginResponse getInstance(UserPrinciple user, String access, String refresh) {
        return SuccessLoginResponse.builder()
                .identity(user.getIdentifier())
                .roles(new HashSet<>(user.getRoles()))
                .access(access)
                .refresh(refresh)
                .build();
    }
    public static SuccessLoginResponse getInstance(UserPrinciple user, String access, String refresh, Collection<? extends ApplicationRole> roles) {
        var combined = new HashSet<ApplicationRole>(user.getRoles());
        combined.addAll(roles);
        return SuccessLoginResponse.builder()
                .identity(user.getIdentifier())
                .roles(combined)
                .access(access)
                .refresh(refresh)
                .build();
    }

}
