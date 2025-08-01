package com.anthonypoon.authenticationserver.domains.twofa;

import com.anthonypoon.authenticationserver.persistence.entity.totp.TOTPDeviceEntity;
import com.anthonypoon.authenticationserver.service.twofa.config.TwoFactorConfig;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Optional;

@Getter
@Builder
public class TOTPDevice {
    private Long id;
    private String issuer;
    private String deviceName;
    private int timeStep;
    private int tokenLength;
    private byte[] secret;
    private Instant createdAt;
    private Instant lastUsedAt;

    public Optional<Instant> getLastUsedAt() {
        return Optional.of(lastUsedAt);
    }

    public static TOTPDevice getInstance(TOTPDeviceEntity device, TwoFactorConfig config, byte[] secret) {
        return TOTPDevice.builder()
                .id(device.getId())
                .issuer(config.getIssuer())
                .deviceName(device.getDeviceName())
                .timeStep(config.getTimeStep())
                .tokenLength(config.getTokenLength())
                .secret(secret)
                .createdAt(Instant.now())
                .lastUsedAt(device.getLastUsedAt())
                .build();
    }
}
