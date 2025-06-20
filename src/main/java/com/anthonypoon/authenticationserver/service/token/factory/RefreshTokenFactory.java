package com.anthonypoon.authenticationserver.service.token.factory;

import com.anthonypoon.authenticationserver.persistence.entity.token.RefreshTokenEntity;
import com.anthonypoon.authenticationserver.persistence.repository.token.RefreshTokenRepository;
import com.anthonypoon.authenticationserver.persistence.repository.user.ApplicationUserRepository;
import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import com.anthonypoon.authenticationserver.service.utils.RandomService;
import com.anthonypoon.authenticationserver.service.token.exception.TokenDecodeException;
import com.anthonypoon.authenticationserver.service.token.exception.TokenEncodeException;
import com.anthonypoon.authenticationserver.service.token.token.RefreshToken;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RefreshTokenFactory extends TokenFactory<RefreshToken, UserPrinciple> {
    private static final int TTL = 24 * 60 * 60;
    private static final int TOKEN_VALUE_LENGTH = 32;
    private final RefreshTokenRepository tokens;
    private final ApplicationUserRepository users;
    private final RandomService random;

    public RefreshTokenFactory(
            RefreshTokenRepository tokens,
            ApplicationUserRepository users,
            RandomService random
    ){
        this.tokens = tokens;
        this.users = users;
        this.random = random;
    }

    @Override
    public Class<RefreshToken> getType() {
        return RefreshToken.class;
    }

    @Override
    public String sign(UserPrinciple user) throws TokenEncodeException {
        var tokenValue = this.random.alphanumeric(TOKEN_VALUE_LENGTH);
        var userEntity = this.users.findByIdentifier(user.getIdentifier())
                .orElseThrow(() -> new TokenEncodeException("Cannot fetch user entity"));
        if (!userEntity.isEnabled() || userEntity.isValidated()) {
            throw new TokenEncodeException("User entity is not active or validated");
        }
        var token = RefreshTokenEntity.builder()
                .tokenValue(tokenValue)
                .expireAt(Instant.now().plusSeconds(TTL))
                .user(userEntity)
                .build();
        this.tokens.save(token);
        return tokenValue;
    }

    @Override
    public RefreshToken decode(String tokenValue) throws TokenDecodeException {
        var token = this.tokens.findByTokenValue(tokenValue, Instant.now())
                .orElseThrow(() -> new TokenDecodeException("Cannot find token by token value"));
        var userEntity = token.getUser();
        if (!userEntity.isEnabled() || userEntity.isValidated()) {
            throw new TokenDecodeException("User entity is not active or validated");
        }
        if (token.getExpireAt().isBefore(Instant.now())) {
            throw new TokenDecodeException("Token is expired");
        }
        return RefreshToken.builder()
                .user(UserPrinciple.getInstance(userEntity))
                .tokenValue(tokenValue)
                .expireAt(token.getExpireAt())
                .build();
    }
}
