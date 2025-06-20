package com.anthonypoon.authenticationserver.service.token.factory;

import com.anthonypoon.authenticationserver.service.token.claim.ClaimEncoder;
import com.anthonypoon.authenticationserver.service.token.exception.TokenDecodeException;
import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import com.anthonypoon.authenticationserver.service.token.token.StepUpChallengeToken;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class StepUpChallengeTokenFactory extends TokenFactory<StepUpChallengeToken, UserPrinciple> {
    private static final int TTL = 10 * 60 * 60;
    private final ClaimEncoder encoder;

    public StepUpChallengeTokenFactory(ClaimEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public Class<StepUpChallengeToken> getType() {
        return StepUpChallengeToken.class;
    }

    @Override
    public String sign(UserPrinciple user) {
        var claim = new HashMap<String, Object>();
        claim.put("identifier", user.getIdentifier());
        return this.encoder.encode(claim, TTL);
    }

    @Override
    public StepUpChallengeToken decode(String tokenValue) throws TokenDecodeException {
        var claims = this.encoder.decode(tokenValue);
        return StepUpChallengeToken.builder()
                .identifier((String) claims.get("identifier"))
                .tokenValue(tokenValue)
                .expireAt(getExpiration(claims))
                .build();
    }
}
