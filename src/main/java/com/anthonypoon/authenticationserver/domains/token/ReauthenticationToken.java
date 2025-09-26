package com.anthonypoon.authenticationserver.domains.token;

import com.anthonypoon.authenticationserver.domains.role.StepUpRole;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class ReauthenticationToken extends Token {
    private String identifier;
    private List<StepUpRole> roles;
}
