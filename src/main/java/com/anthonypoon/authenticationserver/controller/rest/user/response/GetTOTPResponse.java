package com.anthonypoon.authenticationserver.controller.rest.user.response;

import com.anthonypoon.authenticationserver.domains.twofa.TOTPDevice;
import lombok.Builder;
import lombok.Getter;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Builder
public class GetTOTPResponse {
    private Long id;
    private String deviceName;
    private ZonedDateTime createdAt;
    private ZonedDateTime lastUsedAt;

    public static GetTOTPResponse getInstance(TOTPDevice device) {
        return GetTOTPResponse.builder()
                .id(device.getId())
                .deviceName(device.getDeviceName())
                .createdAt(ZonedDateTime.ofInstant(device.getCreatedAt(), ZoneId.systemDefault()))
                .lastUsedAt(
                        device.getLastUsedAt().isEmpty()
                                ? ZonedDateTime.ofInstant(device.getLastUsedAt().get(), ZoneId.systemDefault())
                                : null
                )
                .build();
    }
}
