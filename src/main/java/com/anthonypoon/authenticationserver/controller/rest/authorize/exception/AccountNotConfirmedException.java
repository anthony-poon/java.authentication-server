package com.anthonypoon.authenticationserver.controller.rest.authorize.exception;

import com.anthonypoon.authenticationserver.exception.ErrorType;
import com.anthonypoon.authenticationserver.exception.HttpException;
import com.anthonypoon.authenticationserver.exception.handler.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AccountNotConfirmedException extends HttpException {
    public AccountNotConfirmedException() {
        super("Email account has not been confirmed");
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
                return ErrorType.AccountNotConfirmedError;
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
