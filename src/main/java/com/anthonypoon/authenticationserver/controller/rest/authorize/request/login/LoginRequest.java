package com.anthonypoon.authenticationserver.controller.rest.authorize.request.login;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultLoginRequest.class, name = "DEFAULT"),
        @JsonSubTypes.Type(value = RefreshLoginRequest.class, name = "REFRESH"),
        @JsonSubTypes.Type(value = ReauthenticateLoginRequest.class, name = "REAUTHENTICATE"),
        @JsonSubTypes.Type(value = TOTPLoginRequest.class, name = "TOTP"),
})
public abstract class LoginRequest {
    @NotNull
    public Type type;
    public enum Type {
        DEFAULT,
        REFRESH,
        REAUTHENTICATE,
        TOTP,
    }
}
