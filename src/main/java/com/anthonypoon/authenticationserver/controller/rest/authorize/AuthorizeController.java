package com.anthonypoon.authenticationserver.controller.rest.authorize;

import com.anthonypoon.authenticationserver.controller.rest.authorize.request.*;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.login.*;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.login.LoginResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.registration.RegistrationResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.usecase.LoginUseCase;
import com.anthonypoon.authenticationserver.controller.rest.authorize.usecase.RegistrationUseCase;
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authorize")
public class AuthorizeController {
    private final LoginUseCase logins;
    private final RegistrationUseCase registration;

    public AuthorizeController(LoginUseCase logins, RegistrationUseCase registration) {
        this.logins = logins;
        this.registration = registration;
    }

    @PostMapping("/token")
    public LoginResponse login(@AuthenticationPrincipal UserPrinciple user, @Valid @RequestBody LoginRequest request) {
        return switch (request.getType()) {
            case DEFAULT -> this.logins.login((DefaultLoginRequest) request);
            case REFRESH -> this.logins.refresh((RefreshLoginRequest) request);
            case TOTP -> this.logins.totp((TOTPLoginRequest) request);
            case REAUTHENTICATE -> this.logins.reauthenticate(user, (ReauthenticateLoginRequest) request);
        };
    }

    @PostMapping("/registration")
    public RegistrationResponse registration(@Valid @RequestBody RegistrationRequest request) {
        return this.registration.register(request);
    }

    @PostMapping("/validation")
    public void validation(@Valid @RequestBody ValidationRequest request) {
        this.registration.validation(request);
    }
}
