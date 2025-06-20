package com.anthonypoon.authenticationserver.service.twofa.exception;

public class TOTPException extends Exception {
    public TOTPException(String msg) {
        super(msg);
    }
}
