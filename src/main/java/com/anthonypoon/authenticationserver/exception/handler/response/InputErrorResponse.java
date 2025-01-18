package com.anthonypoon.authenticationserver.exception.handler.response;

import com.anthonypoon.authenticationserver.exception.ErrorType;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class InputErrorResponse implements ErrorResponse {
    private final Map<String, String> errors  = new HashMap<>();
    private final String message;
    private final String errorType;

    public InputErrorResponse(ConstraintViolationException ex) {
        this.errorType = ConstraintViolationException.class.getSimpleName();
        ex.getConstraintViolations().forEach(violation -> {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        });
        if (errors.size() == 1) {
            Map.Entry<String, String> pairs = errors.entrySet().iterator().next();
            this.message = pairs.getValue();
        } else {
            this.message = "One or more fields is invalid";
        }
    }

    public InputErrorResponse(MethodArgumentNotValidException ex) {
        this.errorType = MethodArgumentNotValidException.class.getSimpleName();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        if (errors.size() == 1) {
            Map.Entry<String, String> pairs = errors.entrySet().iterator().next();
            this.message = pairs.getKey() + " " + pairs.getValue();
        } else {
            this.message = "One or more fields is invalid";
        }
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.InputError;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Object getContext() {
        return this.errors;
    }
}
