package com.anthonypoon.authenticationserver.service.email.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.mail")
public class EmailConfig {
    private String from;
    private String baseUrl;
}
