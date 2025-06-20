package com.anthonypoon.authenticationserver.service.token.claim;

import java.util.Map;

public abstract class ClaimEncoder {
    public abstract String encode(Map<String, Object> input, int ttl);
    public abstract Map<String, Object> decode(String input);
}
