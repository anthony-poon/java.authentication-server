package com.anthonypoon.authenticationserver.service.auth.factory;

import com.anthonypoon.authenticationserver.service.auth.token.ReauthenticationToken;
import com.anthonypoon.authenticationserver.service.auth.token.RefreshToken;

import java.util.HashMap;
import java.util.Map;

public class RefreshTokenFactory extends TokenFactory<RefreshToken> {
    @Override
    public Map<String, Object> dehydrate(RefreshToken token) {
        var rtn = new HashMap<String, Object>();
        rtn.put("identifier", token.getIdentifier());
        return super.dehydrate(rtn, token);
    }

    @Override
    public RefreshToken rehydrate(Map<String, Object> claim) {
        var token = RefreshToken.builder()
                .identifier((String) claim.get("identifier"))
                .build();
        return super.rehydrate(claim, token);
    }
}
