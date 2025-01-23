package com.anthonypoon.authenticationserver.controller.rest.user.response;

import com.anthonypoon.authenticationserver.persistence.entity.UserProfile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetProfileResponse {
    private final String displayName;
    private final String email;
    public static GetProfileResponse getInstance(UserProfile profile) {
        return GetProfileResponse.builder()
                .displayName(profile.getDisplayName())
                .email(profile.getUser().getEmail())
                .build();
    }
}
