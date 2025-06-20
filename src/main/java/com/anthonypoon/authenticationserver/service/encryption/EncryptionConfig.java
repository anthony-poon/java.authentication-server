package com.anthonypoon.authenticationserver.service.encryption;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class EncryptionConfig {
    @Value("${application.security.encryption-key}")
    private String key;
}
