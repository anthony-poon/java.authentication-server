package com.anthonypoon.authenticationserver.controller.rest.user;

import com.anthonypoon.authenticationserver.controller.rest.user.request.UpdateProfileRequest;
import com.anthonypoon.authenticationserver.controller.rest.user.response.GetProfileResponse;
import com.anthonypoon.authenticationserver.controller.rest.user.usercase.ProfileUseCase;
import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/profile")
public class ProfileController {
    private final ProfileUseCase profiles;

    public ProfileController(ProfileUseCase profiles) {
        this.profiles = profiles;
    }

    @GetMapping
    public GetProfileResponse getProfile(@AuthenticationPrincipal UserPrinciple user) {
        return this.profiles.get(user);
    }

    @PutMapping
    public void updateProfile(@AuthenticationPrincipal UserPrinciple user, @Valid @RequestBody UpdateProfileRequest request) {
        this.profiles.update(user, request);
    }
}
