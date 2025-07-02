package com.anthonypoon.authenticationserver.controller.rest.user.usercase;

import com.anthonypoon.authenticationserver.controller.rest.user.request.UpdatePasswordRequest;
import com.anthonypoon.authenticationserver.exception.impl.ForbiddenException;
import com.anthonypoon.authenticationserver.exception.impl.InternalServerException;
import com.anthonypoon.authenticationserver.service.auth.UserPrincipleService;
import com.anthonypoon.authenticationserver.service.auth.exception.UserPrincipleException;
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PasswordUseCase {
    private final UserPrincipleService users;
    private final PasswordEncoder encoder;

    public PasswordUseCase(UserPrincipleService users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    public void change(UserPrinciple user, UpdatePasswordRequest request) {
        if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ForbiddenException("Invalid password");
        }
        try {
            this.users.changePassword(user, request.getNewPassword());
        } catch (UserPrincipleException ex) {
            throw new InternalServerException(ex);
        }
    }
}
