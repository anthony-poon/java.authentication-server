package com.anthonypoon.authenticationserver.controller.rest.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterTOTPRequest {
    @NotBlank
    private String deviceName;

}
