package com.anthonypoon.authenticationserver.controller.rest.user.usercase;

import com.anthonypoon.authenticationserver.controller.rest.user.request.UpdateProfileRequest;
import com.anthonypoon.authenticationserver.controller.rest.user.response.GetProfileResponse;
import com.anthonypoon.authenticationserver.exception.impl.ResourceNotFoundException;
import com.anthonypoon.authenticationserver.persistence.entity.UserProfile;
import com.anthonypoon.authenticationserver.persistence.repository.UserProfileRepository;
import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileUseCase {
    private final UserProfileRepository profiles;

    public ProfileUseCase(UserProfileRepository profiles) {
        this.profiles = profiles;
    }

    public GetProfileResponse get(UserPrinciple user) {
        var profile = this.getProfile(user);
        return GetProfileResponse.getInstance(profile);
    }

    @Transactional
    public void update(UserPrinciple user, UpdateProfileRequest request) {
        var profile = this.getProfile(user);
        profile.setDisplayName(request.getDisplayName());
        profile.getUser().setEmail(request.getEmail());
        this.profiles.save(profile);
    }

    private UserProfile getProfile(UserPrinciple user) {
        var profile = this.profiles.findByUserId(user.getId())
                .orElse(null);
        if (profile == null) {
            throw new ResourceNotFoundException();
        }
        return profile;
    }
}
