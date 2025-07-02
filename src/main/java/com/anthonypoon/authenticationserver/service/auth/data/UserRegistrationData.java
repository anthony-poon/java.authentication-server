package com.anthonypoon.authenticationserver.service.auth.data;

import com.anthonypoon.authenticationserver.domains.role.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class UserRegistrationData {
    private final String username;
    private final String password;
    private final String email;
    private final String displayName;
    private final boolean isValidated;
    private final Set<UserRole> roles;
}
