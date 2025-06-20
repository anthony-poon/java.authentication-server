package com.anthonypoon.authenticationserver.service.twofa.data;

import com.anthonypoon.authenticationserver.persistence.entity.totp.TOTPDeviceEntity;
import com.anthonypoon.authenticationserver.service.twofa.config.TwoFactorConfig;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class TOTPDeviceData {
    private Long id;
    private String issuer;
    private String deviceName;
    private int timeStep;
    private int tokenLength;
    private byte[] secret;
    private Instant createdAt;
    private Instant lastUsedAt;

    public static TOTPDeviceData getInstance(TOTPDeviceEntity device, TwoFactorConfig config, byte[] secret) {
        return TOTPDeviceData.builder()
                .id(device.getId())
                .issuer(config.getIssuer())
                .deviceName(device.getDeviceName())
                .timeStep(config.getTimeStep())
                .tokenLength(config.getTokenLength())
                .secret(secret)
                .createdAt(Instant.now())
                .lastUsedAt(null)
                .build();
    }
}
