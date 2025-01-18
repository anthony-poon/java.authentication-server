package com.anthonypoon.authenticationserver.config.security.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Service;

@Service
public class ServiceAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    private static final String HEADER_KEY = "X-API-KEY";

    public ServiceAuthenticationFilter(ServiceAuthenticationManager manager) {
        this.setAuthenticationManager(manager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader(HEADER_KEY);
    }
}
