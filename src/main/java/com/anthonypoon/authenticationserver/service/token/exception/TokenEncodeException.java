package com.anthonypoon.authenticationserver.service.token.exception;

public class TokenEncodeException extends Exception {
    public TokenEncodeException(String message) {
        super(message);
    }

    public TokenEncodeException(String message, Throwable t) {
        super(message, t);
    }
}
