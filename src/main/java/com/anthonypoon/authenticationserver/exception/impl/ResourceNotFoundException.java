package com.anthonypoon.authenticationserver.exception.impl;

import com.anthonypoon.authenticationserver.exception.HttpException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends HttpException {
    public ResourceNotFoundException() {
        super("Resource Not Found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}