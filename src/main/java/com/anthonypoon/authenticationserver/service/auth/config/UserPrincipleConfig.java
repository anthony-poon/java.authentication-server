package com.anthonypoon.authenticationserver.service.auth.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class UserPrincipleConfig {
    @Value("${application.authorization.require-validation:false}")
    private boolean validationRequired;
}
