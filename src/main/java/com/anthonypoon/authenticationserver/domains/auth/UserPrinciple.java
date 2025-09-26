package com.anthonypoon.authenticationserver.domains.auth;

import com.anthonypoon.authenticationserver.domains.role.UserRole;
import com.anthonypoon.authenticationserver.persistence.entity.user.ApplicationUserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class UserPrinciple {
    private final Long id;
    private final String identifier;
    private final String password;
    private final String email;
    private final boolean isEnabled;
    private final boolean isValidated;
    private final Set<UserRole> roles;
    public static UserPrinciple getInstance(ApplicationUserEntity user) {
        return UserPrinciple.builder()
                .id(user.getId())
                .identifier(user.getIdentifier())
                .password(user.getPassword())
                .email(user.getEmail())
                .isEnabled(user.isEnabled())
                .isValidated(user.isValidated())
                .roles(user.getRoles())
                .build();
    }
}
