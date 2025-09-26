package com.anthonypoon.authenticationserver.service.token.factory;

import com.anthonypoon.authenticationserver.persistence.entity.token.RefreshTokenEntity;
import com.anthonypoon.authenticationserver.persistence.repository.token.RefreshTokenRepository;
import com.anthonypoon.authenticationserver.persistence.repository.user.ApplicationUserRepository;
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.service.auth.policy.UserPrinciplePolicy;
import com.anthonypoon.authenticationserver.service.token.decoder.ClaimEncoder;
import com.anthonypoon.authenticationserver.service.token.exception.TokenDecodeException;
import com.anthonypoon.authenticationserver.service.token.exception.TokenEncodeException;
import com.anthonypoon.authenticationserver.domains.token.RefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;

@Component
public class RefreshTokenFactory extends TokenFactory<RefreshToken, UserPrinciple> {
    private static final int TTL = 24 * 60 * 60;
    private final RefreshTokenRepository tokens;
    private final ApplicationUserRepository users;
    private final UserPrinciplePolicy policy;
    private final ClaimEncoder encoder;

    public RefreshTokenFactory(
            RefreshTokenRepository tokens,
            ApplicationUserRepository users,
            UserPrinciplePolicy policy, ClaimEncoder encoder
    ){
        this.tokens = tokens;
        this.users = users;
        this.policy = policy;
        this.encoder = encoder;
    }

    @Override
    public Class<RefreshToken> getType() {
        return RefreshToken.class;
    }

    @Override
    @Transactional
    public String sign(UserPrinciple user) throws TokenEncodeException {
        var claim = new HashMap<String, Object>();
        claim.put("identifier", user.getIdentifier());
        if (policy.isInvalid(user)) {
            throw new TokenEncodeException("User entity is not active or validated");
        }
        var tokenValue = this.encoder.encode(claim, TTL);
        this.tokens.invalidate(user.getIdentifier(), Instant.now());
        var token = RefreshTokenEntity.builder()
                .tokenValue(tokenValue)
                .identifier(user.getIdentifier())
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
        var user = UserPrinciple.getInstance(userEntity);
        if (policy.isInvalid(user)) {
            throw new TokenDecodeException("User entity is not active or validated");
        }
        if (token.getExpireAt().isBefore(Instant.now())) {
            throw new TokenDecodeException("Token is expired");
        }
        return RefreshToken.builder()
                .user(user)
                .tokenValue(tokenValue)
                .expireAt(token.getExpireAt())
                .build();
    }
}
