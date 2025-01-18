package com.anthonypoon.authenticationserver.controller.rest.authorize.usecase;

import com.anthonypoon.authenticationserver.config.auth.AuthorizationConfig;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.DefaultTokenRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.RefreshTokenRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.GetTokenResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.CallbackTokenResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.SuccessTokenResponse;
import com.anthonypoon.authenticationserver.exception.impl.BadRequestException;
import com.anthonypoon.authenticationserver.exception.impl.ForbiddenException;
import com.anthonypoon.authenticationserver.exception.impl.UnauthorizedException;
import com.anthonypoon.authenticationserver.service.auth.AuthTokenService;
import com.anthonypoon.authenticationserver.service.auth.UserPrincipleService;
import com.anthonypoon.authenticationserver.service.auth.exception.AuthTokenException;
import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import com.anthonypoon.authenticationserver.service.auth.token.RefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@Slf4j
public class GetTokenUseCase {
    private final UserPrincipleService users;
    private final AuthTokenService tokens;
    private final PasswordEncoder encoder;
    private final AuthorizationConfig config;

    public GetTokenUseCase(
            UserPrincipleService users,
            AuthTokenService tokens,
            PasswordEncoder encoder,
            AuthorizationConfig config
    ) {
        this.users = users;
        this.tokens = tokens;
        this.encoder = encoder;
        this.config = config;
    }

    public GetTokenResponse login(DefaultTokenRequest request) {
        var user = this.users.getByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            log.debug("User not found by username");
            throw new UnauthorizedException("Incorrect username or password.");
        }

        this.validate(user);
        if (!this.encoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Incorrect username or password.");
        }
        if (StringUtils.isEmpty(request.getCallback())) {
            var accessToken = this.tokens.signAccessToken(user);
            var refreshToken = this.tokens.signRefreshToken(user);
            return SuccessTokenResponse.builder()
                    .identity(user.getIdentifier())
                    .roles(user.getRoles())
                    .access(accessToken.getTokenValue())
                    .refresh(refreshToken.getTokenValue())
                    .build();
        } else {
            var token = this.tokens.signRefreshToken(user);
            return CallbackTokenResponse.builder()
                    .callback(callback(request, token))
                    .build();
        }
    }

    public GetTokenResponse refresh(RefreshTokenRequest request) {
        RefreshToken token;
        try {
            token = this.tokens.rehydrate(request.getToken(), RefreshToken.class);
        } catch (AuthTokenException ex) {
            log.debug("Unable to parse refresh token. Reason: %s".formatted(ex.getMessage()));
            throw new UnauthorizedException("Invalid refresh token");
        }
        var user = this.users.getByIdentifier(token.getIdentifier()).orElse(null);
        if (user == null) {
            log.debug("User not found by username");
            throw new UnauthorizedException("Incorrect username or password.");
        }
        this.validate(user);
        var accessToken = this.tokens.signAccessToken(user);
        return SuccessTokenResponse.builder()
                .identity(user.getIdentifier())
                .roles(user.getRoles())
                .access(accessToken.getTokenValue())
                .refresh(request.getToken())
                .build();
    }

//    public GetTokenResponse reauthenticate(UserPrinciple principle, ReauthenticateTokenRequest request) {
//        var user = this.users.getByIdentifier(principle.getIdentifier()).orElse(null);
//        if (user == null) {
//            log.debug("User not found by username");
//            throw new UnauthorizedException("Incorrect username or password.");
//        }
//        this.validate(user);
//        if (!this.encoder.matches(request.getPassword(), user.getPassword())) {
//            throw new UnauthorizedException("Incorrect username or password.");
//        }
//        try {
//            var token = this.tokens.signReauthenticateToken(principle);
//            return ReauthenticateTokenResponse.getInstance(token);
//        } catch (AuthTokenException ex) {
//            throw new InternalServerException(ex);
//        }
//    }

    private void validate(UserPrinciple user) {
        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled.");
        }
        if (!user.isValidated()) {
            throw new UnauthorizedException("Account have not been validated");
        }
    }

    private String callback(DefaultTokenRequest request, RefreshToken token) {
        if (StringUtils.isEmpty(request.getCallback())) {
            return null;
        }
        var match = this.config.getCallbacks().stream()
                .anyMatch(r -> request.getCallback().startsWith(r));

        if (!match) {
            throw new ForbiddenException("Forbidden callbacks");
        }
        try {
            var origin = new URI(request.getCallback());
            var rtn = new URI(
                    origin.getScheme(),
                    origin.getAuthority(),
                    origin.getPath(),
                    origin.getQuery() == null ?
                            origin.getQuery() + "?token=" + token.getTokenValue() :
                            origin.getQuery() + "&token=" + token.getTokenValue(),
                    origin.getFragment()
            );
            // TODO: Need to force state query?
            return rtn.toString();
        } catch (URISyntaxException e) {
            throw new BadRequestException("Invalid callback");
        }
    }
}
