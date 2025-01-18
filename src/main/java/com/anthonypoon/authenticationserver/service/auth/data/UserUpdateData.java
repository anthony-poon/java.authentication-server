package com.anthonypoon.authenticationserver.service.auth.data;

import com.anthonypoon.authenticationserver.constant.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class UserUpdateData {
    private final Long userId;
    private final String password;
    private final boolean isEnabled;
    private final boolean isValidated;
    private final Set<UserRole> roles;
}
