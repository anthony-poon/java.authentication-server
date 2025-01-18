package com.anthonypoon.authenticationserver.exception.impl;

import com.anthonypoon.authenticationserver.exception.HttpException;
import org.springframework.http.HttpStatus;

public class ResourceAlreadyCreatedException extends HttpException {
    public ResourceAlreadyCreatedException() {
        super("Resource Already Created");
    }

    public ResourceAlreadyCreatedException(String message) {
        super(message);
    }

    public ResourceAlreadyCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceAlreadyCreatedException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}