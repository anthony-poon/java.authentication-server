package com.anthonypoon.authenticationserver.domains.token;

import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class RefreshToken extends Token {
    private UserPrinciple user;
}
