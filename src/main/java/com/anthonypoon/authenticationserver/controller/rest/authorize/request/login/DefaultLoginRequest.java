package com.anthonypoon.authenticationserver.controller.rest.authorize.request.login;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultLoginRequest extends LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String callback;
}
