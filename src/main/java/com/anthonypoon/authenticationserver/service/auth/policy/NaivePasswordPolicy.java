package com.anthonypoon.authenticationserver.service.auth.policy;

import com.anthonypoon.authenticationserver.service.auth.exception.UserPrinciplePasswordException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class NaivePasswordPolicy implements PasswordPolicy {
    private static final int MIN_LENGTH = 8;
    @Override
    public void validate(String password) throws UserPrinciplePasswordException {
        if (password == null) {
            throw new UserPrinciplePasswordException("Password cannot be empty.");
        }

        var hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        var hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        var hasDigit = password.chars().anyMatch(Character::isDigit);
        var hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*()".indexOf(ch) >= 0);
        var isLong = password.length() >= MIN_LENGTH;
        var isValid = hasUppercase && hasLowercase && hasDigit && hasSpecialChar && isLong;
        if (!isValid) {
            throw new UserPrinciplePasswordException("Password does not match password policy.");
        }
    }
}
