package com.anthonypoon.authenticationserver.controller.rest.authorize;

import com.anthonypoon.authenticationserver.controller.rest.authorize.request.DefaultLoginRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.LoginRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.RefreshLoginRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.GetTokenResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.usecase.LoginUseCase;
import com.anthonypoon.authenticationserver.exception.impl.BadRequestException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authorize")
public class AuthorizeController {
    private final LoginUseCase logins;

    public AuthorizeController(LoginUseCase logins) {
        this.logins = logins;
    }

    @PostMapping("/token")
    public GetTokenResponse login(@Valid @RequestBody LoginRequest request) {
        return switch (request.getType()) {
            case DEFAULT -> this.logins.login((DefaultLoginRequest) request);
            case REFRESH -> this.logins.refresh((RefreshLoginRequest) request);
//            case REAUTHENTICATE -> this.tokens.reauthenticate(principle, (ReauthenticateTokenRequest) request, query);
            default -> throw new BadRequestException("Unsupported type");
        };
    }
}
