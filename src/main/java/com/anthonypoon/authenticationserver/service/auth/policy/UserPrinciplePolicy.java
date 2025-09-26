package com.anthonypoon.authenticationserver.service.auth.policy;

import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.service.auth.config.UserPrincipleConfig;
import org.springframework.stereotype.Component;

@Component
public class UserPrinciplePolicy {
    public final UserPrincipleConfig config;

    public UserPrinciplePolicy(UserPrincipleConfig config) {
        this.config = config;
    }

    public boolean isInvalid(UserPrinciple user) {
        if (!user.isEnabled()) {
            return true;
        }
        if (this.config.isValidationRequired() && !user.isValidated()) {
            return true;
        }
        return false;
    }
}
