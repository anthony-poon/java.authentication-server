package com.anthonypoon.authenticationserver.config.security.web;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebAuthenticationFilter  extends AbstractPreAuthenticatedProcessingFilter {
    private static final String HEADER_KEY = "Authorization";
    private static final Pattern PATTERN = Pattern.compile("Bearer (.+)");

    public WebAuthenticationFilter(WebAuthenticationManager manager) {
        this.setAuthenticationManager(manager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String value = request.getHeader(HEADER_KEY);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        Matcher matcher = PATTERN.matcher(value);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return null;
    }
}
