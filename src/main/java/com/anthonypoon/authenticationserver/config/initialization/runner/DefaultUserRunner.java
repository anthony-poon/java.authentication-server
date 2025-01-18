package com.anthonypoon.authenticationserver.config.initialization.runner;

import com.anthonypoon.authenticationserver.config.initialization.InitializationConfig;
import com.anthonypoon.authenticationserver.service.auth.UserPrincipleService;
import com.anthonypoon.authenticationserver.service.auth.data.UserRegistrationData;
import com.anthonypoon.authenticationserver.service.auth.exception.UserPrincipleDuplicatedException;
import com.anthonypoon.authenticationserver.service.auth.exception.UserPrinciplePasswordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class DefaultUserRunner implements InitializationRunner {
    private final UserPrincipleService users;
    private final InitializationConfig config;

    public DefaultUserRunner(UserPrincipleService users, InitializationConfig config) {
        this.users = users;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            log.info("User initialization runner loaded.");
            this.createUsers();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to initialize application.", ex);
        }
    }

    private void createUsers() throws UserPrincipleDuplicatedException, UserPrinciplePasswordException {
        var entries = this.config.getUsers();
        if (entries == null || entries.isEmpty()) {
            return;
        }
        log.info("Creating default user; count=%s".formatted(entries.size()));
        var data = entries.stream()
                .map(entry -> UserRegistrationData.builder()
                        .username(entry.getUsername())
                        .password(entry.getPassword())
                        .roles(entry.getRoles())
                        .isValidated(true)
                        .build())
                .toList();
        this.users.register(data);
    }
}
