package com.anthonypoon.authenticationserver.controller.rest.authorize.usecase;

import com.anthonypoon.authenticationserver.controller.rest.authorize.config.AuthorizationConfig;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.login.DefaultLoginRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.login.ReauthenticateLoginRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.login.RefreshLoginRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.request.login.TOTPLoginRequest;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.login.LoginResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.login.CallbackLoginResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.login.RequireTwoFALoginResponse;
import com.anthonypoon.authenticationserver.controller.rest.authorize.response.login.SuccessLoginResponse;
import com.anthonypoon.authenticationserver.domains.role.StepUpRole;
import com.anthonypoon.authenticationserver.exception.impl.BadRequestException;
import com.anthonypoon.authenticationserver.exception.impl.ForbiddenException;
import com.anthonypoon.authenticationserver.exception.impl.InternalServerException;
import com.anthonypoon.authenticationserver.exception.impl.UnauthorizedException;
import com.anthonypoon.authenticationserver.service.auth.policy.UserPrinciplePolicy;
import com.anthonypoon.authenticationserver.service.twofa.policy.TwoFactorPolicy;
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
import java.util.List;

@Service
@Slf4j
public class LoginUseCase {
    private final UserPrincipleService users;
    private final TokenService tokens;
    private final PasswordEncoder encoder;
    private final AuthorizationConfig config;
    private final TwoFactorPolicy twoFactorPolicy;
    private final UserPrinciplePolicy userPolicy;
    private final TOTPService totp;

    public LoginUseCase(
            UserPrincipleService users,
            TokenService tokens,
            PasswordEncoder encoder,
            AuthorizationConfig config,
            TwoFactorPolicy twoFactorPolicy,
            UserPrinciplePolicy userPolicy,
            TOTPService totp
    ) {
        this.users = users;
        this.tokens = tokens;
        this.encoder = encoder;
        this.config = config;
        this.twoFactorPolicy = twoFactorPolicy;
        this.userPolicy = userPolicy;
        this.totp = totp;
    }

    public LoginResponse login(DefaultLoginRequest request) {
        var user = this.users.getByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            log.debug("User not found by username");
            throw new UnauthorizedException("Incorrect username or password.");
        }

        if (this.userPolicy.isInvalid(user)) {
            throw new UnauthorizedException("Account is disabled or have not been validated");
        }
        if (!this.encoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Incorrect username or password.");
        }
        if (this.twoFactorPolicy.isRequired(user)) {
            return RequireTwoFALoginResponse.builder()
                    .challenge(this.tokens.signTwoFAChallengeToken(user))
                    .build();
        }
        try {
            if (StringUtils.isEmpty(request.getCallback())) {
                var access = this.tokens.signAccessToken(user);
                var refresh = this.tokens.signRefreshToken(user);
                return SuccessLoginResponse.getInstance(user, access, refresh);
            } else {
                var refresh = this.tokens.signRefreshToken(user);
                return CallbackLoginResponse.builder()
                        .callback(getCallbackUrl(request, refresh))
                        .build();
            }
        } catch (TokenEncodeException ex) {
            throw new InternalServerException("Unable to sign refresh tokens", ex);
        }
    }

    public LoginResponse refresh(RefreshLoginRequest request) {
        RefreshToken token;
        try {
            token = this.tokens.decode(request.getToken(), RefreshToken.class);
        } catch (TokenDecodeException ex) {
            log.debug("Unable to parse refresh token. Reason: %s".formatted(ex.getMessage()));
            throw new UnauthorizedException("Invalid refresh token");
        }
        var user = token.getUser();
        if (this.userPolicy.isInvalid(user)) {
            throw new UnauthorizedException("Account is disabled or have not been validated");
        }
        var access = this.tokens.signAccessToken(user);
        return SuccessLoginResponse.getInstance(user, access, request.getToken());
    }

    public LoginResponse totp(TOTPLoginRequest request) {
        TwoFAChallengeToken token;
        try {
            token = this.tokens.decode(request.getChallenge(), TwoFAChallengeToken.class);
        } catch (TokenDecodeException ex) {
            throw new ForbiddenException("Invalid totp token");
        }
        var user = this.users.getByIdentifier(token.getIdentifier())
                .orElseThrow(() -> new ForbiddenException("Invalid user."));
        if (this.userPolicy.isInvalid(user)) {
            throw new UnauthorizedException("Account is disabled or have not been validated");
        }
        var isValid = this.totp.checkToken(user, request.getCode());
        if (!isValid) {
            throw new ForbiddenException("Invalid code.");
        }

        try {
            var access = tokens.signAccessToken(user);
            var refresh = tokens.signRefreshToken(user);
            return SuccessLoginResponse.getInstance(user, access, refresh);
        } catch (TokenEncodeException e) {
            throw new RuntimeException(e);
        }

    }

    public LoginResponse reauthenticate(UserPrinciple user, ReauthenticateLoginRequest request) {
        if (this.userPolicy.isInvalid(user)) {
            throw new UnauthorizedException("Account is disabled or have not been validated");
        }
        if (!this.encoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Incorrect username or password.");
        }
        try {
            var access = tokens.signAccessToken(user);
            var refresh = tokens.signRefreshToken(user);
            return SuccessLoginResponse.getInstance(user, access, refresh, List.of(StepUpRole.REAUTHENTICATED_ACCESS));
        } catch (TokenEncodeException ex) {
            throw new InternalServerException(ex);
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
