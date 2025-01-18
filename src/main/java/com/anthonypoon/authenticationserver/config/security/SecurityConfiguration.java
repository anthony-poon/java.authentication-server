package com.anthonypoon.authenticationserver.config.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SecurityConfiguration {
    @Value("${application.security.allowed-origins}")
    private String[] allowedOrigins;
}
