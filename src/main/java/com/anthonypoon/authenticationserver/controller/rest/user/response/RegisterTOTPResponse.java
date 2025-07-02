package com.anthonypoon.authenticationserver.controller.rest.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterTOTPResponse {
    private Long id;
    private String url;
    private String secret;
}
