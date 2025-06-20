package com.anthonypoon.authenticationserver.service.token.token;

import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class RefreshToken extends Token {
    private UserPrinciple user;
}
