package com.anthonypoon.authenticationserver.controller.rest.authorize.request.login;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TOTPLoginRequest extends LoginRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String challenge;
}
