package com.anthonypoon.authenticationserver.controller.rest.authorize.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest extends TokenRequest {
    @NotBlank
    private String token;
}
