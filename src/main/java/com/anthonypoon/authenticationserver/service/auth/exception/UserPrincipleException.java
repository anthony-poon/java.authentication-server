package com.anthonypoon.authenticationserver.service.auth.exception;

import lombok.Getter;

@Getter
public class UserPrincipleException extends Exception {
    public UserPrincipleException(String message) {
        super(message);
    }
}
