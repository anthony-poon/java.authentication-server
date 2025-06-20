package com.anthonypoon.authenticationserver.service.token.exception;

public class TokenDecodeException extends Exception {
    public TokenDecodeException(String message) {
        super(message);
    }

    public TokenDecodeException(String message, Throwable t) {
        super(message, t);
    }
}
