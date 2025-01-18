package com.anthonypoon.authenticationserver.service.auth.principle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ServicePrincipal {
    private final String name;
}
