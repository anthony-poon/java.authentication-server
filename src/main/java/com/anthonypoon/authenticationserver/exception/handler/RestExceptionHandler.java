package com.anthonypoon.authenticationserver.exception.handler;

import com.anthonypoon.authenticationserver.exception.HttpException;
import com.anthonypoon.authenticationserver.exception.handler.response.ErrorResponse;
import com.anthonypoon.authenticationserver.exception.handler.response.InputErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {
            HttpException.class
    })
    public ResponseEntity<ErrorResponse> resourceNotFoundResponse(HttpException ex, WebRequest request) {
        if (ex.getHttpStatus().is5xxServerError()) {
            log.error(ex.getMessage(), ex);
        } else if (ex.getHttpStatus().is4xxClientError()) {
            log.info(ex.getMessage());
        }
        return new ResponseEntity<>(ex.getResponse(), ex.getHttpStatus());
    }

    @ExceptionHandler(value = {
            ConstraintViolationException.class,
    })
    public ResponseEntity<InputErrorResponse> userInputResponse(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>(new InputErrorResponse(ex), BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<InputErrorResponse> methodArgumentResponse(MethodArgumentNotValidException ex, WebRequest request) {
        return new ResponseEntity<>(new InputErrorResponse(ex), BAD_REQUEST);
    }
}
