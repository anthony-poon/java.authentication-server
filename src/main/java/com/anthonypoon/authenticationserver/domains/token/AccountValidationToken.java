package com.anthonypoon.authenticationserver.domains.token;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccountValidationToken extends Token {
    private String identifier;
}
