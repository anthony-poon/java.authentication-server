package com.anthonypoon.authenticationserver.service.twofa.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TwoFactorConfig {
    private static final int TOTP_TIME_STEP = 30;
    private static final int TOTP_TOKEN_LENGTH = 6;
    private static final int TOTP_RECOVERY_COUNT = 10;
    private static final String TOTP_ALGO = "HmacSHA1";

    @Value("${application.two-factor.requirement:NONE}")
    private Requirement requirement;

    @Value("${spring.application.name}")
    private String issuer;
    public enum Requirement {
        NONE,
        OPTIONAL,
        REQUIRED
    }

    public int getTimeStep() {
        return TOTP_TIME_STEP;
    }

    public int getTokenLength() {
        return TOTP_TOKEN_LENGTH;
    }

    public String getAlgo() {
        return TOTP_ALGO;
    }

    public int getTotpRecoveryCount() {
        return TOTP_RECOVERY_COUNT;
    }
}
