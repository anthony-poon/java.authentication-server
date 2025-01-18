package com.anthonypoon.authenticationserver.service.auth.exception;

public class AuthTokenException extends Exception {
    public AuthTokenException(String message) {
        super(message);
    }

    public AuthTokenException(String message, Throwable t) {
        super(message, t);
    }
}
