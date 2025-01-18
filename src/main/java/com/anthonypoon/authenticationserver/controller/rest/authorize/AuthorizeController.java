package com.anthonypoon.authenticationserver.controller.rest.authorize;

import com.anthonypoon.authenticationserver.controller.rest.authorize.request.DefaultTokenRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.TokenRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.RefreshTokenRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.GetTokenResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.usecase.GetTokenUseCase;
import com.anthonypoon.authenticationserver.exception.impl.BadRequestException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authorize")
public class AuthorizeController {
    private final GetTokenUseCase tokens;

    public AuthorizeController(GetTokenUseCase tokens) {
        this.tokens = tokens;
    }

    @PostMapping("/token")
    public GetTokenResponse login(@Valid @RequestBody TokenRequest request) {
        return switch (request.getType()) {
            case DEFAULT -> this.tokens.login((DefaultTokenRequest) request);
            case REFRESH -> this.tokens.refresh((RefreshTokenRequest) request);
//            case REAUTHENTICATE -> this.tokens.reauthenticate(principle, (ReauthenticateTokenRequest) request, query);
            default -> throw new BadRequestException("Unsupported type");
        };
    }
}
