package com.anthonypoon.authenticationserver.service.auth.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Configuration
@AllArgsConstructor
@ConfigurationProperties(prefix = "application.services")
public class ServicePrincipleConfig {
    private final List<Key> keys;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Key {
        private final String name;
        private final String value;
    }
}
