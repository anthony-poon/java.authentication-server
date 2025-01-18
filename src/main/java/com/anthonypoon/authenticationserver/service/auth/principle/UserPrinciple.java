package com.anthonypoon.authenticationserver.service.auth.principle;

import com.anthonypoon.authenticationserver.constant.UserRole;
import com.anthonypoon.authenticationserver.persistence.entity.ApplicationUser;
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
    private final boolean isEnabled;
    private final boolean isValidated;
    private final Set<UserRole> roles;
    public static UserPrinciple getInstance(ApplicationUser user) {
        return UserPrinciple.builder()
                .id(user.getId())
                .identifier(user.getIdentifier())
                .password(user.getPassword())
                .isEnabled(user.isEnabled())
                .isValidated(user.isValidated())
                .roles(user.getRoles())
                .build();
    }
}
