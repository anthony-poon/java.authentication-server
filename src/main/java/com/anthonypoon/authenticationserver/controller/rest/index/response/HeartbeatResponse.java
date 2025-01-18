package com.anthonypoon.authenticationserver.controller.rest.index.response;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class HeartbeatResponse {
    private String application;
    private ZonedDateTime timestamp;
}
