package com.anthonypoon.authenticationserver.service.token.factory;

import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.domains.token.AccountValidationToken;
import com.anthonypoon.authenticationserver.domains.token.Token;
import com.anthonypoon.authenticationserver.persistence.entity.token.AccountValidationTokenEntity;
import com.anthonypoon.authenticationserver.persistence.repository.token.AccountValidationTokenRepository;
import com.anthonypoon.authenticationserver.service.token.exception.TokenDecodeException;
import com.anthonypoon.authenticationserver.service.utils.RandomService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;

@Service
public class AccountValidationTokenFactory extends TokenFactory<AccountValidationToken, UserPrinciple> {
    private static final int TTL_SECONDS = 60 * 60;
    private static final int RANDOM_LENGTH = 32;
    private final AccountValidationTokenRepository tokens;
    private final RandomService random;

    public AccountValidationTokenFactory(AccountValidationTokenRepository tokens, RandomService random) {
        this.tokens = tokens;
        this.random = random;
    }
    @Override
    public Class<? extends Token> getType() {
        return AccountValidationToken.class;
    }

    @Override
    public String sign(UserPrinciple context) {
        var existing = this.tokens.findAllByIdentifier(context.getIdentifier()).stream()
                .min(Comparator.comparing(AccountValidationTokenEntity::getCreatedAt));
        if (existing.isPresent()) {
            return existing.get().getTokenValue();
        }
        var secret = random.secure(RANDOM_LENGTH);
        var encode = new String(Base64.getEncoder().encode(secret));
        var entity = AccountValidationTokenEntity.builder()
                .tokenValue(encode)
                .identifier(context.getIdentifier())
                .expireAt(Instant.now().plusSeconds(TTL_SECONDS))
                .build();
        this.tokens.save(entity);
        return encode;
    }

    @Override
    public AccountValidationToken decode(String tokenValue) throws TokenDecodeException {
        var existing = this.tokens.findByTokenValue(tokenValue);
        if (existing.isEmpty()) {
            throw new TokenDecodeException("Invalid token value");
        }
        var token = existing.get();
        return AccountValidationToken.builder()
                .identifier(token.getIdentifier())
                .tokenValue(tokenValue)
                .expireAt(token.getExpireAt())
                .build();
    }
}
