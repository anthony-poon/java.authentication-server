package com.anthonypoon.authenticationserver.config.security.web;

import com.anthonypoon.authenticationserver.constant.TemporaryRole;
import com.anthonypoon.authenticationserver.service.auth.AuthTokenService;
import com.anthonypoon.authenticationserver.service.auth.UserPrincipleService;
import com.anthonypoon.authenticationserver.service.auth.exception.AuthTokenException;
import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import com.anthonypoon.authenticationserver.service.auth.token.AccessToken;
import com.anthonypoon.authenticationserver.service.auth.token.ReauthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Primary
public class WebAuthenticationManager implements AuthenticationManager {
    private final AuthTokenService tokens;
    private final UserPrincipleService users;
    private final HttpServletRequest requests;

    public WebAuthenticationManager(AuthTokenService tokens, UserPrincipleService users, HttpServletRequest requests) {
        this.tokens = tokens;
        this.users = users;
        this.requests = requests;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String jws = (String) authentication.getPrincipal();
        if (StringUtils.isEmpty(jws)) {
            throw new BadCredentialsException("Missing jws token");
        }
        try {
            var token = tokens.rehydrate(jws, AccessToken.class);
            var user = users.getByIdentifier(token.getIdentifier()).orElse(null);
            if (user == null) {
                throw new BadCredentialsException("Invalid user identifier.");
            }
            if (!user.isEnabled()) {
                throw new DisabledException("User is disabled");
            }
            return new PreAuthenticatedAuthenticationToken(
                    user,
                    jws,
                    this.getAuthority(user)
            );
        } catch (AuthTokenException ex) {
            throw new BadCredentialsException("Invalid token.");
        }
    }

    private Collection<SimpleGrantedAuthority> getAuthority(UserPrinciple user) {
        var roles = user.getRoles();
        var auths = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toSet());
        this.reauthenticateIfExist(auths);
        return auths.stream().map(auth -> new SimpleGrantedAuthority(auth.toString())).toList();
    }

    private void reauthenticateIfExist(Set<SimpleGrantedAuthority> roles) {
        try {
            var header = requests.getHeader("X-Reauthenticate-Token");
            if (StringUtils.isEmpty(header)) {
                return;
            }
            var token = this.tokens.rehydrate(header, ReauthenticationToken.class);
            var user = this.users.getByIdentifier(token.getIdentifier()).orElse(null);
            if (user == null) {
                throw new BadCredentialsException("Invalid user identifier.");
            }
            if (!user.isEnabled()) {
                throw new DisabledException("User is disabled");
            }
            roles.add(new SimpleGrantedAuthority(TemporaryRole.REAUTHENTICATED_ACCESS.toString()));
        } catch (AuthTokenException ex) {
            throw new BadCredentialsException("Invalid token.");
        }

    }
}
