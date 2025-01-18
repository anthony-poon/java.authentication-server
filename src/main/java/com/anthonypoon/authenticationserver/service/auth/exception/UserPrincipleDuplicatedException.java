package com.anthonypoon.authenticationserver.service.auth.exception;

import lombok.Getter;

@Getter
public class UserPrincipleDuplicatedException extends UserPrincipleException {
    private final Type type;
    public UserPrincipleDuplicatedException(String message, Type type) {
        super(message);
        this.type = type;
    }
    public enum Type {
        USERNAME,
        EMAIL
    }
}
