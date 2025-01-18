package com.anthonypoon.authenticationserver.exception.impl;

import com.anthonypoon.authenticationserver.exception.HttpException;
import org.springframework.http.HttpStatus;

public class InternalServerException extends HttpException {
    public InternalServerException() {
        super("Internal Server Error");
    }

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
