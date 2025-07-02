package com.anthonypoon.authenticationserver.service.token.factory;

import com.anthonypoon.authenticationserver.service.token.decoder.ClaimEncoder;
import com.anthonypoon.authenticationserver.service.token.exception.TokenDecodeException;
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.domains.token.AccessToken;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class AccessTokenFactory extends TokenFactory<AccessToken, UserPrinciple> {
    private static final int TTL = 10 * 60 * 60;
    private final ClaimEncoder encoder;

    public AccessTokenFactory(ClaimEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public Class<AccessToken> getType() {
        return AccessToken.class;
    }

    @Override
    public String sign(UserPrinciple user) {
        var claim = new HashMap<String, Object>();
        claim.put("identifier", user.getIdentifier());
        return this.encoder.encode(claim, TTL);
    }

    @Override
    public AccessToken decode(String tokenValue) throws TokenDecodeException {
        var claims = this.encoder.decode(tokenValue);
        return AccessToken.builder()
                .identifier((String) claims.get("identifier"))
                .tokenValue(tokenValue)
                .expireAt(getExpiration(claims))
                .build();
    }
}
