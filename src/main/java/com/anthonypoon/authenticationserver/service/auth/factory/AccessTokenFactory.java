package com.anthonypoon.authenticationserver.service.auth.factory;

import com.anthonypoon.authenticationserver.service.auth.token.AccessToken;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class AccessTokenFactory extends TokenFactory<AccessToken> {
    @Override
    public Map<String, Object> dehydrate(AccessToken token) {
        var rtn = new HashMap<String, Object>();
        rtn.put("identifier", token.getIdentifier());
        return super.dehydrate(rtn, token);
    }

    @Override
    public AccessToken rehydrate(Map<String, Object> claim) {
        var token = AccessToken.builder()
                .identifier((String) claim.get("identifier"))
                .build();
        return super.rehydrate(claim, token);
    }
}
