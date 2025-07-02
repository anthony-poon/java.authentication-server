package com.anthonypoon.authenticationserver.controller.rest.authorize.usecase;

import com.anthonypoon.authenticationserver.controller.rest.authorize.config.AuthorizationConfig;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.DefaultLoginRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.RefreshLoginRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.TOTPLoginRequest;
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
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.service.token.exception.TokenEncodeException;
import com.anthonypoon.authenticationserver.domains.token.RefreshToken;
import com.anthonypoon.authenticationserver.domains.token.TwoFAChallengeToken;
import com.anthonypoon.authenticationserver.service.twofa.TOTPService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@Slf4j
public class LoginUseCase {
    private final UserPrincipleService users;
    private final TokenService tokens;
    private final PasswordEncoder encoder;
    private final AuthorizationConfig config;
    private final TOTPService twoFactors;

    public LoginUseCase(
            UserPrincipleService users,
            TokenService tokens,
            PasswordEncoder encoder,
            AuthorizationConfig config,
            TOTPService twoFactors) {
        this.users = users;
        this.tokens = tokens;
        this.encoder = encoder;
        this.config = config;
        this.twoFactors = twoFactors;
    }

    public GetTokenResponse login(DefaultLoginRequest request) {
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
                var access = this.tokens.signAccessToken(user);
                var refresh = this.tokens.signRefreshToken(user);
                return SuccessTokenResponse.builder()
                        .identity(user.getIdentifier())
                        .roles(user.getRoles())
                        .access(access)
                        .refresh(refresh)
                        .build();
            } else {
                var refresh = this.tokens.signRefreshToken(user);
                return CallbackTokenResponse.builder()
                        .callback(getCallbackUrl(request, refresh))
                        .build();
            }
        } catch (TokenEncodeException ex) {
            throw new InternalServerException("Unable to sign refresh tokens", ex);
        }
    }

    public GetTokenResponse refresh(RefreshLoginRequest request) {
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

    public GetTokenResponse totp(TOTPLoginRequest request) {
        TwoFAChallengeToken token;
        try {
            token = this.tokens.decode(request.getChallenge(), TwoFAChallengeToken.class);
        } catch (TokenDecodeException ex) {
            throw new ForbiddenException("Invalid totp token");
        }
        var user = this.users.getByIdentifier(token.getIdentifier())
                .orElseThrow(() -> new ForbiddenException("Invalid user."));
        validate(user);
        var isValid = this.twoFactors.checkToken(user, request.getTokenValue());
        if (!isValid) {
            throw new ForbiddenException("Invalid code.");
        }

        try {
            var access = tokens.signAccessToken(user);
            var refresh = tokens.signRefreshToken(user);
            return SuccessTokenResponse.builder()
                    .identity(user.getIdentifier())
                    .roles(user.getRoles())
                    .access(access)
                    .refresh(refresh)
                    .build();
        } catch (TokenEncodeException e) {
            throw new RuntimeException(e);
        }

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

    private String getCallbackUrl(DefaultLoginRequest request, String token) {
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
