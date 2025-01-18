package com.anthonypoon.authenticationserver.service.auth.factory;

import com.anthonypoon.authenticationserver.service.auth.token.AccessToken;
import com.anthonypoon.authenticationserver.service.auth.token.ReauthenticationToken;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class ReauthenticateTokenFactory extends TokenFactory<ReauthenticationToken> {
    @Override
    public Map<String, Object> dehydrate(ReauthenticationToken token) {
        var rtn = new HashMap<String, Object>();
        rtn.put("identifier", token.getIdentifier());
        return super.dehydrate(rtn, token);
    }

    @Override
    public ReauthenticationToken rehydrate(Map<String, Object> claim) {
        var token = ReauthenticationToken.builder()
                .identifier((String) claim.get("identifier"))
                .build();
        return super.rehydrate(claim, token);
    }
}
