package com.anthonypoon.authenticationserver.domains.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ServicePrincipal {
    private final String name;
}
