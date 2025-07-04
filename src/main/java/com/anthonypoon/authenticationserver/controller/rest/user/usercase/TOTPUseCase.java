package com.anthonypoon.authenticationserver.controller.rest.user.usercase;

import com.anthonypoon.authenticationserver.controller.rest.user.request.RegisterTOTPRequest;
import com.anthonypoon.authenticationserver.controller.rest.user.request.ValidateTOTPRequest;
import com.anthonypoon.authenticationserver.controller.rest.user.response.GetTOTPRecoveryResponse;
import com.anthonypoon.authenticationserver.controller.rest.user.response.GetTOTPResponse;
import com.anthonypoon.authenticationserver.controller.rest.user.response.RegisterTOTPResponse;
import com.anthonypoon.authenticationserver.controller.rest.user.response.ValidateTOTPResponse;
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.exception.impl.BadRequestException;
import com.anthonypoon.authenticationserver.service.twofa.TOTPService;
import com.anthonypoon.authenticationserver.service.twofa.exception.TOTPException;
import com.google.common.io.BaseEncoding;
import org.apache.catalina.util.URLEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TOTPUseCase {
    private final TOTPService totp;
    private final URLEncoder encoder;

    public TOTPUseCase(TOTPService totp) {
        this.totp = totp;
        this.encoder = new URLEncoder();
    }

    public List<GetTOTPResponse> getDevices(UserPrinciple user) {
        var devices = this.totp.getDevices(user);
        return devices.stream()
                .map(GetTOTPResponse::getInstance)
                .collect(Collectors.toList());
    }

    public RegisterTOTPResponse register(UserPrinciple user, RegisterTOTPRequest request) {
        var device = this.totp.register(user, request.getDeviceName());
        var secret = BaseEncoding.base32().encode(device.getSecret()).replaceAll("=+$", "");
        var url = String.format(
                "otpauth://totp/%s?secret=%s&issuer=%s&digits=%d&period=%d",
                encoder.encode(device.getIssuer(), StandardCharsets.UTF_8),
                secret,
                encoder.encode(device.getIssuer(), StandardCharsets.UTF_8),
                device.getTokenLength(),
                device.getTimeStep()
        );
        return RegisterTOTPResponse.builder()
                .id(device.getId())
                .url(url)
                .secret(secret)
                .build();
    }

    public ValidateTOTPResponse validate(Long id, UserPrinciple user , ValidateTOTPRequest request) {
        var success = this.totp.validate(id, user, request.getCode());
        if (!success) {
            throw new BadRequestException("Cannot validate device. Please check your code.");
        }
        var recoveries = this.totp.getRecoveries(user);
        // If there are no recoveries for the user, generate some, otherwise return nothing
        if (recoveries.isEmpty()) {
            recoveries = this.totp.generateRecoveries(user);
            return ValidateTOTPResponse.builder()
                    .recoveries(recoveries)
                    .build();
        } else {
            return ValidateTOTPResponse.builder()
                    .recoveries(new ArrayList<>())
                    .build();
        }
    }

    public List<GetTOTPRecoveryResponse> regenerateRecoveries(UserPrinciple user) {
        var devices = this.totp.getDevices(user);
        if (devices.size() == 0) {
            throw new BadRequestException("No TOTP Device associated with this user");
        }
        var recoveries = this.totp.regenerateRecoveries(user);
        return recoveries.stream().map(recovery -> GetTOTPRecoveryResponse.builder()
                        .code(recovery)
                        .build())
                .collect(Collectors.toList());
    }

    public void unregister(UserPrinciple user, Long id) {
        try {
            this.totp.unregister(user, id);
        } catch (TOTPException ex) {
            throw new BadRequestException(ex.getMessage(), ex);
        }
    }
}
