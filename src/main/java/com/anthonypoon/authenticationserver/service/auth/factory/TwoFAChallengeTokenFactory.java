package com.anthonypoon.authenticationserver.service.auth.factory;

import com.anthonypoon.authenticationserver.service.auth.token.TwoFAChallengeToken;

import java.util.HashMap;
import java.util.Map;

public class TwoFAChallengeTokenFactory extends TokenFactory<TwoFAChallengeToken> {
    @Override
    public Map<String, Object> dehydrate(TwoFAChallengeToken token) {
        var rtn = new HashMap<String, Object>();
        rtn.put("identifier", token.getIdentifier());
        return super.dehydrate(rtn, token);
    }

    @Override
    public TwoFAChallengeToken rehydrate(Map<String, Object> claim) {
        var token = TwoFAChallengeToken.builder()
                .identifier((String) claim.get("identifier"))
                .build();
        return super.rehydrate(claim, token);
    }
}
