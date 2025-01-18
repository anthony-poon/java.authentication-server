package com.anthonypoon.authenticationserver.config.security.service;

import com.anthonypoon.authenticationserver.service.auth.ServicePrincipleService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class ServiceAuthenticationManager implements AuthenticationManager {
    private final ServicePrincipleService services;

    public ServiceAuthenticationManager(ServicePrincipleService services) {
        this.services = services;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var apiKey = (String) authentication.getCredentials();
        var principle = this.services.loadByKey(apiKey).orElse(null);
        if (principle == null) {
            throw new BadCredentialsException("Invalid API Key");
        }
        return new PreAuthenticatedAuthenticationToken(principle.getName(), apiKey);
    }
}
