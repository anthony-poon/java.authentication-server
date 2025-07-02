package com.anthonypoon.authenticationserver.service.twofa.policy;

import com.anthonypoon.authenticationserver.persistence.repository.totp.TOTPDeviceEntityRepository;
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.service.twofa.config.TwoFactorConfig;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorPolicy {
    private final TwoFactorConfig config;
    private final TOTPDeviceEntityRepository totps;

    public TwoFactorPolicy(
            TwoFactorConfig configs,
            TOTPDeviceEntityRepository totps
    ) {
        this.config = configs;
        this.totps = totps;
    }

    public boolean isRequired(UserPrinciple user) {
        return switch (config.getRequirement()) {
            case NONE -> false;
            case REQUIRED -> true;
            case OPTIONAL -> this.totps.existsByUserId(user.getId());
        };
    }
}
