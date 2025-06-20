package com.anthonypoon.authenticationserver.controller.rest.user;

import com.anthonypoon.authenticationserver.controller.rest.user.request.UpdatePasswordRequest;
import com.anthonypoon.authenticationserver.controller.rest.user.request.UpdateProfileRequest;
import com.anthonypoon.authenticationserver.controller.rest.user.response.GetProfileResponse;
import com.anthonypoon.authenticationserver.controller.rest.user.usercase.PasswordUseCase;
import com.anthonypoon.authenticationserver.controller.rest.user.usercase.ProfileUseCase;
import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/profile")
public class ProfileController {
    private final ProfileUseCase profiles;
    private final PasswordUseCase password;

    public ProfileController(ProfileUseCase profiles, PasswordUseCase password) {
        this.profiles = profiles;
        this.password = password;
    }

    @GetMapping
    public GetProfileResponse getProfile(@AuthenticationPrincipal UserPrinciple user) {
        return this.profiles.get(user);
    }

    @PutMapping
    public void updateProfile(@AuthenticationPrincipal UserPrinciple user, @Valid @RequestBody UpdateProfileRequest request) {
        this.profiles.update(user, request);
    }

    @PutMapping("/password")
    public void updatePassword(@AuthenticationPrincipal UserPrinciple user, @Valid @RequestBody UpdatePasswordRequest request) {
        this.password.change(user, request);
    }
}
