package com.anthonypoon.authenticationserver.service.token.factory;

import com.anthonypoon.authenticationserver.persistence.entity.token.RefreshTokenEntity;
import com.anthonypoon.authenticationserver.persistence.repository.token.RefreshTokenRepository;
import com.anthonypoon.authenticationserver.persistence.repository.user.ApplicationUserRepository;
import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import com.anthonypoon.authenticationserver.service.token.claim.ClaimEncoder;
import com.anthonypoon.authenticationserver.service.utils.RandomService;
import com.anthonypoon.authenticationserver.service.token.exception.TokenDecodeException;
import com.anthonypoon.authenticationserver.service.token.exception.TokenEncodeException;
import com.anthonypoon.authenticationserver.service.token.token.RefreshToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;

@Component
public class RefreshTokenFactory extends TokenFactory<RefreshToken, UserPrinciple> {
    private static final int TTL = 24 * 60 * 60;
    private final RefreshTokenRepository tokens;
    private final ApplicationUserRepository users;
    private final ClaimEncoder encoder;

    public RefreshTokenFactory(
            RefreshTokenRepository tokens,
            ApplicationUserRepository users,
            ClaimEncoder encoder
    ){
        this.tokens = tokens;
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public Class<RefreshToken> getType() {
        return RefreshToken.class;
    }

    @Override
    public String sign(UserPrinciple user) throws TokenEncodeException {
        var claim = new HashMap<String, Object>();
        claim.put("identifier", user.getIdentifier());
        var tokenValue = this.encoder.encode(claim, TTL);
        var userEntity = this.users.findByIdentifier(user.getIdentifier())
                .orElseThrow(() -> new TokenEncodeException("Cannot fetch user entity"));
        if (!userEntity.isEnabled() || !userEntity.isValidated()) {
            throw new TokenEncodeException("User entity is not active or validated");
        }
        var token = RefreshTokenEntity.builder()
                .tokenValue(tokenValue)
                .expireAt(Instant.now().plusSeconds(TTL))
                .build();
        this.tokens.save(token);
        return tokenValue;
    }

    @Override
    public RefreshToken decode(String tokenValue) throws TokenDecodeException {
        var token = this.tokens.findByTokenValue(tokenValue, Instant.now())
                .orElseThrow(() -> new TokenDecodeException("Cannot find token by token value"));
        var claims = this.encoder.decode(tokenValue);
        var identifier = claims.get("identifier").toString();
        var userEntity = this.users.findByIdentifier(identifier)
                .orElseThrow(() -> new TokenDecodeException("Cannot fetch user by identifier"));
        if (!userEntity.isEnabled() || !userEntity.isValidated()) {
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
