package com.anthonypoon.authenticationserver.domains.token;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccessToken extends Token {
    private final String identifier;
}
