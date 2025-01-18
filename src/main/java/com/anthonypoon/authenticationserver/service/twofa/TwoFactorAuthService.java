package com.anthonypoon.authenticationserver.service.twofa;

import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorAuthService {
    public boolean isTwoFARequired(UserPrinciple principle) {
        return false;
    }
}
