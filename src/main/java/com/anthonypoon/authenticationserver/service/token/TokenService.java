package com.anthonypoon.authenticationserver.service.token;

import com.anthonypoon.authenticationserver.domains.token.*;
import com.anthonypoon.authenticationserver.service.token.exception.TokenDecodeException;
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.service.token.exception.TokenEncodeException;
import com.anthonypoon.authenticationserver.service.token.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private final Map<Class<? extends Token>, TokenFactory<?, ?>> factories;
    @Autowired
    public TokenService(List<TokenFactory<?, ?>> factories) {
        this.factories = factories.stream()
                .collect(Collectors.toMap(TokenFactory::getType, Function.identity()));
    }

    public String signAccessToken(UserPrinciple user) {
        var factory = (AccessTokenFactory) this.getFactory(AccessToken.class);
        return factory.sign(user);
    }

    public String signRefreshToken(UserPrinciple user) throws TokenEncodeException {
        var factory = (RefreshTokenFactory) this.getFactory(RefreshToken.class);
        return factory.sign(user);
    }

    public String signReauthenticationToken(UserPrinciple user) {
        var factory = (ReauthenticationTokenFactory) this.getFactory(ReauthenticationToken.class);
        return factory.sign(user);
    }

    public String signTwoFAChallengeToken(UserPrinciple user) {
        var factory = (TwoFAChallengeTokenFactory) this.getFactory(TwoFAChallengeToken.class);
        return factory.sign(user);
    }

    public String signAccountConfirmationToken(UserPrinciple user) {
        var factory = (AccountValidationTokenFactory) this.getFactory(AccountValidationToken.class);
        return factory.sign(user);
    }

    public <T extends Token> T decode(String tokenValue, Class<T> tokenClass) throws TokenDecodeException {
        TokenFactory<T, ?> factory = this.getFactory(tokenClass);
        return factory.decode(tokenValue);
    }

    @SuppressWarnings("unchecked")
    private <T extends Token> TokenFactory<T, ?> getFactory(Class<T> tokenClass) {
        TokenFactory<?, ?> factory = this.factories.get(tokenClass);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported token of class: " + tokenClass);
        }
        return (TokenFactory<T, ?>) factory;
    }
}
