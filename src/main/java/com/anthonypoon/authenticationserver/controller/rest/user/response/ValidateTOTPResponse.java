package com.anthonypoon.authenticationserver.controller.rest.user.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ValidateTOTPResponse {
    private List<String> recoveries;
}
