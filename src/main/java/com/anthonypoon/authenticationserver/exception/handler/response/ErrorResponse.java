package com.anthonypoon.authenticationserver.exception.handler.response;

import com.anthonypoon.authenticationserver.exception.ErrorType;

public interface ErrorResponse {
    ErrorType getErrorType();
    String getMessage();
    Object getContext();
}
