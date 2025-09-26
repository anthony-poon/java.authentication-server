package com.anthonypoon.authenticationserver.service.token.factory;

import com.anthonypoon.authenticationserver.service.token.decoder.ClaimEncoder;
import com.anthonypoon.authenticationserver.service.token.exception.TokenDecodeException;
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.domains.token.ReauthenticationToken;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ReauthenticationTokenFactory extends TokenFactory<ReauthenticationToken, UserPrinciple> {
    private static final int TTL = 10 * 60 * 60;
    private final ClaimEncoder encoder;

    public ReauthenticationTokenFactory(ClaimEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public Class<ReauthenticationToken> getType() {
        return ReauthenticationToken.class;
    }

    @Override
    public String sign(UserPrinciple user) {
        var claim = new HashMap<String, Object>();
        claim.put("identifier", user.getIdentifier());
        return this.encoder.encode(claim, TTL);
    }

    @Override
    public ReauthenticationToken decode(String tokenValue) throws TokenDecodeException {
        var claims = this.encoder.decode(tokenValue);
        return ReauthenticationToken.builder()
                .identifier((String) claims.get("identifier"))
                .tokenValue(tokenValue)
                .expireAt(getExpiration(claims))
                .build();
    }
}
