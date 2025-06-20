package com.anthonypoon.authenticationserver.controller.rest.authorize.usecase;

import com.anthonypoon.authenticationserver.config.auth.AuthorizationConfig;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.DefaultTokenRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.RefreshTokenRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.GetTokenResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.CallbackTokenResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.SuccessTokenResponse;
import com.anthonypoon.authenticationserver.exception.impl.BadRequestException;
import com.anthonypoon.authenticationserver.exception.impl.ForbiddenException;
import com.anthonypoon.authenticationserver.exception.impl.InternalServerException;
import com.anthonypoon.authenticationserver.exception.impl.UnauthorizedException;
import com.anthonypoon.authenticationserver.service.token.TokenService;
import com.anthonypoon.authenticationserver.service.auth.UserPrincipleService;
import com.anthonypoon.authenticationserver.service.token.exception.TokenDecodeException;
import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import com.anthonypoon.authenticationserver.service.token.exception.TokenEncodeException;
import com.anthonypoon.authenticationserver.service.token.token.RefreshToken;
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
    private final TokenService tokens;
    private final PasswordEncoder encoder;
    private final AuthorizationConfig config;

    public GetTokenUseCase(
            UserPrincipleService users,
            TokenService tokens,
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
        try {
            if (StringUtils.isEmpty(request.getCallback())) {
                var accessToken = this.tokens.signAccessToken(user);
                var refreshToken = this.tokens.signRefreshToken(user);
                return SuccessTokenResponse.builder()
                        .identity(user.getIdentifier())
                        .roles(user.getRoles())
                        .access(accessToken)
                        .refresh(refreshToken)
                        .build();
            } else {
                var token = this.tokens.signRefreshToken(user);
                return CallbackTokenResponse.builder()
                        .callback(callback(request, token))
                        .build();
            }
        } catch (TokenEncodeException ex) {
            throw new InternalServerException("Unable to sign refresh tokens", ex);
        }

    }

    public GetTokenResponse refresh(RefreshTokenRequest request) {
        RefreshToken token;
        try {
            token = this.tokens.decode(request.getToken(), RefreshToken.class);
        } catch (TokenDecodeException ex) {
            log.debug("Unable to parse refresh token. Reason: %s".formatted(ex.getMessage()));
            throw new UnauthorizedException("Invalid refresh token");
        }
        var user = token.getUser();
        this.validate(user);
        var accessToken = this.tokens.signAccessToken(user);
        return SuccessTokenResponse.builder()
                .identity(user.getIdentifier())
                .roles(user.getRoles())
                .access(accessToken)
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

    private String callback(DefaultTokenRequest request, String token) {
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
                            origin.getQuery() + "?token=" + token :
                            origin.getQuery() + "&token=" + token,
                    origin.getFragment()
            );
            // TODO: Need to force state query?
            return rtn.toString();
        } catch (URISyntaxException e) {
            throw new BadRequestException("Invalid callback");
        }
    }
}
