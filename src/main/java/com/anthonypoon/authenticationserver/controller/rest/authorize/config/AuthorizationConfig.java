package com.anthonypoon.authenticationserver.controller.rest.authorize.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.authorization")
public class AuthorizationConfig {
    private String secret;
    private List<String> callbacks;
}
