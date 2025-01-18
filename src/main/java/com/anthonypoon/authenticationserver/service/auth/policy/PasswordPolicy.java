package com.anthonypoon.authenticationserver.service.auth.policy;

import com.anthonypoon.authenticationserver.service.auth.exception.UserPrinciplePasswordException;
import org.springframework.stereotype.Component;

@Component
public interface PasswordPolicy {
    void validate(String password) throws UserPrinciplePasswordException;
}
