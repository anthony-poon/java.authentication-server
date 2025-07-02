package com.anthonypoon.authenticationserver.controller.rest.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTOTPRecoveryResponse {
    private String code;
}
