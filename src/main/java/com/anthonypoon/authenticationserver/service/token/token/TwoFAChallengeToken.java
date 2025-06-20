package com.anthonypoon.authenticationserver.service.token.token;

import com.anthonypoon.authenticationserver.service.token.token.Token;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class TwoFAChallengeToken extends Token {
    private final String identifier;
}
