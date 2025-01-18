package com.anthonypoon.authenticationserver.controller.rest.authorize.exception;

import com.anthonypoon.authenticationserver.exception.ErrorType;
import com.anthonypoon.authenticationserver.exception.HttpException;
import com.anthonypoon.authenticationserver.exception.handler.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AccountDisabledException extends HttpException {
    public AccountDisabledException() {
        super("Account has been disabled");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public ErrorResponse getResponse() {
        var message = this.getMessage();
        return new ErrorResponse() {
            @Override
            public ErrorType getErrorType() {
                return ErrorType.AccountDisabledError;
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
