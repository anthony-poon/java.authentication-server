package com.anthonypoon.authenticationserver.service.auth;

import com.anthonypoon.authenticationserver.service.auth.factory.*;
import com.anthonypoon.authenticationserver.service.auth.encoder.Encoder;
import com.anthonypoon.authenticationserver.service.auth.exception.AuthTokenException;
import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import com.anthonypoon.authenticationserver.service.auth.token.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthTokenService {
    private static final Map<AuthToken.Type, TokenFactory<? extends AuthToken>> TOKEN_TYPE_MAP;
    private static final Map<AuthToken.Type, Integer> TOKEN_TTL_MAP;
    private final Encoder encoder;

    static {
        TOKEN_TYPE_MAP = new HashMap<>();
        TOKEN_TTL_MAP = new HashMap<>();
        TOKEN_TYPE_MAP.put(AuthToken.Type.ACCESS, new AccessTokenFactory());
        TOKEN_TYPE_MAP.put(AuthToken.Type.REFRESH, new RefreshTokenFactory());
        TOKEN_TYPE_MAP.put(AuthToken.Type.REAUTHENTICATE, new ReauthenticateTokenFactory());
        TOKEN_TYPE_MAP.put(AuthToken.Type.TWO_FA_CHALLENGE, new TwoFAChallengeTokenFactory());
        TOKEN_TTL_MAP.put(AuthToken.Type.ACCESS, 6 * 60);
        TOKEN_TTL_MAP.put(AuthToken.Type.REFRESH, 24 * 60);
        TOKEN_TTL_MAP.put(AuthToken.Type.REAUTHENTICATE, 60);
        TOKEN_TTL_MAP.put(AuthToken.Type.TWO_FA_CHALLENGE, 60);
    }

    // TODO: Merge all token into one with different role
    public AccessToken signAccessToken(UserPrinciple user) {
        var token = AccessToken.builder()
                .identifier(user.getIdentifier())
                .tokenValue(null)
                .build();
        var tokenValue = this.dehydrate(token, TOKEN_TTL_MAP.get(token.getType()));
        token.setTokenValue(tokenValue);
        return token;
    }

    public RefreshToken signRefreshToken(UserPrinciple user) {
        var token = RefreshToken.builder()
                .identifier(user.getIdentifier())
                .tokenValue(null)
                .build();
        var tokenValue = this.dehydrate(token, TOKEN_TTL_MAP.get(token.getType()));
        token.setTokenValue(tokenValue);
        return token;
    }

    public ReauthenticationToken signReauthenticateToken(UserPrinciple user) {
        var token = ReauthenticationToken.builder()
                .identifier(user.getIdentifier())
                .tokenValue(null)
                .build();
        var tokenValue = this.dehydrate(token, TOKEN_TTL_MAP.get(token.getType()));
        token.setTokenValue(tokenValue);
        return token;
    }

    public TwoFAChallengeToken signTwoFAToken(UserPrinciple user) {
        var token = TwoFAChallengeToken.builder()
                .identifier(user.getIdentifier())
                .tokenValue(null)
                .build();
        var tokenValue = this.dehydrate(token, TOKEN_TTL_MAP.get(token.getType()));
        token.setTokenValue(tokenValue);
        return token;
    }

    @Autowired
    public AuthTokenService(Encoder encoder) {
        this.encoder = encoder;
    }

    public String dehydrate(AuthToken token, int ttl) {
        var factory = this.getFactory(token.getType());
        var claims = factory.dehydrate(token);
        return this.encoder.encode(claims, ttl);
    }

    public <T extends AuthToken> T rehydrate(String input, Class<T> tokenClass) throws AuthTokenException {
        var claims = this.encoder.decode(input);
        var type = claims.get("_type");
        if (type == null) {
            throw new AuthTokenException("Missing token type information");
        }
        var factory = this.getFactory(AuthToken.Type.valueOf(type.toString()));
        var token = factory.rehydrate(claims);
        token.setTokenValue(input);
        if (!tokenClass.isInstance(token)) {
            throw new AuthTokenException(
                    String.format("Expected token of type %s but got %s",
                            tokenClass.getSimpleName(),
                            token.getClass().getSimpleName())
            );
        }
        return tokenClass.cast(token);
    }

    private <T extends AuthToken> TokenFactory<T> getFactory(AuthToken.Type type) {
        @SuppressWarnings("unchecked")
        TokenFactory<T> factory = (TokenFactory<T>) TOKEN_TYPE_MAP.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported token class of type: " + type);
        }
        return factory;
    }
}
