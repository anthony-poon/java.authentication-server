package com.anthonypoon.authenticationserver.config.initialization;

import com.anthonypoon.authenticationserver.domains.role.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "application.initialization")
public class InitializationConfig {
    private List<UserEntry> users;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserEntry {
        private String username;
        private String password;
        private Set<UserRole> roles;
    }
}
