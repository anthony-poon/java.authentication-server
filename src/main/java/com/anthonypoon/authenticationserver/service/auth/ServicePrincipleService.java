package com.anthonypoon.authenticationserver.service.auth;

import com.anthonypoon.authenticationserver.service.auth.config.ServicePrincipleConfig;
import com.anthonypoon.authenticationserver.domains.auth.ServicePrincipal;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServicePrincipleService {
    private final ServicePrincipleConfig config;

    public ServicePrincipleService(ServicePrincipleConfig config) {
        this.config = config;
    }

    public Optional<ServicePrincipal> loadByKey(String apiKey) {
        var key = config.getKeys().stream()
                .filter(k -> k.getValue().equals(apiKey))
                .findFirst()
                .orElse(null);
        if (key == null) {
            return Optional.empty();
        }
        return Optional.of(
                ServicePrincipal.builder()
                        .name(key.getName())
                        .build()
        );
    }
}
