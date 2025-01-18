package com.anthonypoon.authenticationserver.service.auth.encoder;

import java.util.Map;

public abstract class Encoder {
    public abstract String encode(Map<String, Object> input, int ttl);
    public abstract Map<String, Object> decode(String input);
}
