package com.anthonypoon.authenticationserver.service.auth;

import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.domains.token.AccountValidationToken;
import com.anthonypoon.authenticationserver.persistence.repository.user.ApplicationUserRepository;
import com.anthonypoon.authenticationserver.service.email.EmailService;
import com.anthonypoon.authenticationserver.service.email.mail.AccountConfirmationMail;
import com.anthonypoon.authenticationserver.service.token.TokenService;
import com.anthonypoon.authenticationserver.service.token.exception.TokenDecodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
public class UserPrincipleValidationService {
    private final ApplicationUserRepository users;
    private final TokenService tokens;
    private final EmailService emails;

    public UserPrincipleValidationService(
            ApplicationUserRepository users,
            TokenService tokens,
            EmailService emails
    ) {
        this.users = users;
        this.tokens = tokens;
        this.emails = emails;
    }

    @Transactional
    public void trigger(UserPrinciple user) {

        var tokenValue = this.tokens.signAccountConfirmationToken(user);
        var mail = AccountConfirmationMail.builder()
                .tokenValue(tokenValue)
                .subject("Account confirmation")
                .recipients(Set.of(user.getEmail()))
                .build();
        this.emails.send(mail);
        log.info("Confirmation email send to %s".formatted(user.getEmail()));
    }

    @Transactional
    public boolean tryValidate(String tokenValue) {
        try {
            var token = this.tokens.decode(tokenValue, AccountValidationToken.class);
            var existing = this.users.findByIdentifier(token.getIdentifier());
            if (existing.isEmpty()) {
                return false;
            }
            var user = existing.get();
            user.setValidated(true);
            this.users.save(user);
            return true;
        } catch (TokenDecodeException ex) {
            log.debug(ex.getMessage());
            log.info("Unable to decode token %s".formatted(tokenValue));
            return false;
        }
    }
}
