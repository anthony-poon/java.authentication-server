package com.anthonypoon.authenticationserver.controller.rest.authorize.response;

import com.anthonypoon.authenticationserver.service.token.token.StepUpChallengeToken;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ReauthenticateTokenResponse extends GetTokenResponse {

    private String token;

    public static ReauthenticateTokenResponse getInstance(StepUpChallengeToken token) {
        return ReauthenticateTokenResponse.builder()
                .token(token.getTokenValue())
                .build();
    }

    @Override
    public Type getType() {
        return Type.REAUTHENTICATED;
    }
}
