package com.anthonypoon.authenticationserver.controller.rest.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProfileRequest {
    @NotBlank
    private String displayName;
    @NotBlank
    @Email
    private String email;
}
