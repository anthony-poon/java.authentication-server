package com.anthonypoon.authenticationserver.exception;

import com.anthonypoon.authenticationserver.exception.handler.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public abstract class HttpException extends RuntimeException {
    public HttpException() {
        super();
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }
    public abstract HttpStatus getHttpStatus();

    public ErrorResponse getResponse() {
        String message = this.getMessage();
        return new ErrorResponse() {
            @Override
            public ErrorType getErrorType() {
                return ErrorType.GenericError;
            }

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public Object getContext() {
                return null;
            }
        };
    }
}
