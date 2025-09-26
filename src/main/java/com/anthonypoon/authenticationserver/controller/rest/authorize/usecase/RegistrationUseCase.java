package com.anthonypoon.authenticationserver.controller.rest.authorize.usecase;

import com.anthonypoon.authenticationserver.controller.rest.authorize.request.RegistrationRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.ValidationRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.registration.RegistrationResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.registration.RequireValidationResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.registration.SuccessRegistrationResponse;
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.domains.role.UserRole;
import com.anthonypoon.authenticationserver.exception.impl.BadRequestException;
import com.anthonypoon.authenticationserver.exception.impl.InternalServerException;
import com.anthonypoon.authenticationserver.service.auth.UserPrincipleService;
import com.anthonypoon.authenticationserver.service.auth.UserPrincipleValidationService;
import com.anthonypoon.authenticationserver.service.auth.config.UserPrincipleConfig;
import com.anthonypoon.authenticationserver.service.auth.data.UserRegistrationData;
import com.anthonypoon.authenticationserver.service.auth.exception.UserPrincipleDuplicatedException;
import com.anthonypoon.authenticationserver.service.auth.exception.UserPrincipleException;
import com.anthonypoon.authenticationserver.service.token.TokenService;
import com.anthonypoon.authenticationserver.service.token.exception.TokenEncodeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class RegistrationUseCase {
    private final UserPrincipleService users;
    private final UserPrincipleValidationService validations;
    private final TokenService tokens;
    private final UserPrincipleConfig config;


    public RegistrationUseCase(
            UserPrincipleService users,
            UserPrincipleValidationService validations,
            TokenService tokens,
            UserPrincipleConfig config
    ) {
        this.users = users;
        this.validations = validations;
        this.tokens = tokens;
        this.config = config;
    }

    @Transactional
    public RegistrationResponse register(RegistrationRequest request) {
        try {
            var datum = UserRegistrationData.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .displayName(request.getDisplayName())
                    .roles(Set.of(UserRole.USER))
                    .isValidated(false)
                    .build();
            var user = this.users.register(datum);
            if (config.isValidationRequired()) {
                this.validations.trigger(user);
                return RequireValidationResponse.builder().build();
            } else {
                return this.success(user);
            }
        } catch (UserPrincipleDuplicatedException e) {
            throw new BadRequestException("Username or email already registered", e);
        } catch (UserPrincipleException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    public void validation(ValidationRequest request) {
        var success = this.validations.tryValidate(request.getTokenValue());
        if (!success) {
            throw new BadRequestException("Invalid token");
        }
    }

    private SuccessRegistrationResponse success(UserPrinciple user) {
        try {
            var refresh = this.tokens.signRefreshToken(user);
            return SuccessRegistrationResponse.builder()
                    .refresh(refresh)
                    .build();
        } catch (TokenEncodeException e) {
            throw new InternalServerException(e.getMessage(), e);
        }
    }
}
