package com.anthonypoon.authenticationserver.service.token.token;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
public abstract class Token {
    private String tokenValue;

    private Instant expireAt;
}
