package com.anthonypoon.authenticationserver.controller.rest.authorize.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultTokenRequest extends TokenRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String callback;
}
