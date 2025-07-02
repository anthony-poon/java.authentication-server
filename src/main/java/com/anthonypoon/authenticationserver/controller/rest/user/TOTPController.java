package com.anthonypoon.authenticationserver.controller.rest.user;

import com.anthonypoon.authenticationserver.controller.rest.user.request.RegisterTOTPRequest;
import com.anthonypoon.authenticationserver.controller.rest.user.request.ValidateTOTPRequest;
import com.anthonypoon.authenticationserver.controller.rest.user.response.GetTOTPRecoveryResponse;
import com.anthonypoon.authenticationserver.controller.rest.user.response.GetTOTPResponse;
import com.anthonypoon.authenticationserver.controller.rest.user.response.RegisterTOTPResponse;
import com.anthonypoon.authenticationserver.controller.rest.user.response.ValidateTOTPResponse;
import com.anthonypoon.authenticationserver.controller.rest.user.usercase.TOTPUseCase;
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/totp")
public class TOTPController {
    private final TOTPUseCase totp;

    public TOTPController(TOTPUseCase totp) {
        this.totp = totp;
    }

    @GetMapping()
    public List<GetTOTPResponse> getDevices(@AuthenticationPrincipal UserPrinciple user) {
        return this.totp.getDevices(user);
    }

    @PostMapping
    public RegisterTOTPResponse register(
            @AuthenticationPrincipal UserPrinciple user,
            @Valid @RequestBody RegisterTOTPRequest request
    ) {
        return this.totp.register(user, request);
    }

    @PostMapping("/{id}/validation")
    public ValidateTOTPResponse validate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrinciple user,
            @Valid @RequestBody ValidateTOTPRequest request
    ) {
        return this.totp.validate(id, user, request);
    }

    @PostMapping("/recoveries")
    public List<GetTOTPRecoveryResponse> regenerateRecovery(@AuthenticationPrincipal UserPrinciple user) {
        return this.totp.regenerateRecoveries(user);
    }

    @DeleteMapping("/{id}")
    public void unregister(@AuthenticationPrincipal UserPrinciple user, @PathVariable Long id) {
        this.totp.unregister(user, id);
    }
}
