package com.anthonypoon.authenticationserver.exception.impl;

import com.anthonypoon.authenticationserver.exception.HttpException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends HttpException {
    public BadRequestException() {
        super("Bad Request");
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
