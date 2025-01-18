package com.anthonypoon.authenticationserver.exception.impl;

import com.anthonypoon.authenticationserver.exception.HttpException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends HttpException {
    public ForbiddenException() {
        super("Forbidden");
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
