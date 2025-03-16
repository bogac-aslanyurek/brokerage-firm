package com.ing.brokeragefirm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.text.MessageFormat;

@ControllerAdvice(basePackages = "com.ing")
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handle(ApiException ex, WebRequest request) {

        log.debug("ApiException occurred code: {}, message: {}", ex.getCode(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(this.toResponse(ex));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handle(ConstraintViolationException ex, WebRequest request) {
        log.debug("ConstraintViolationException occurred: {}", ex.getMessage(), ex);

        // Extract violations and construct a user-friendly message
        StringBuilder message = new StringBuilder("Validation failed for the following fields: ");
        ex.getConstraintViolations().forEach(violation -> {
            message.append(String.format("%s (invalid value: %s): %s; ",
                    violation.getPropertyPath().toString(),
                    violation.getInvalidValue(),
                    violation.getMessage()));
        });

        // Trim trailing semicolon and space; fallback to a default message if needed
        String userFriendlyMessage = message.length() > 0
                ? message.substring(0, message.length() - 2)
                : "Invalid request parameters";

        // Return a response entity with the user-friendly message
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(this.toResponse(new ApiException(1000, userFriendlyMessage)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handle(Exception ex, WebRequest request) {

        log.debug("Unexpected exception occurred!", ex.getStackTrace());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(this.toResponse(new ApiException(1000, "System error occurred.")));
    }

    public ApiError toResponse(ApiException ex) {

        ApiError response = new ApiError();
        response.setCode(ex.getCode());
        response.setMessage(MessageFormat.format(ex.getMessage(), (Object[]) ex.getArgs()));
        return response;
    }

    public ApiError toResponse(Exception ex) {
        ApiError response = new ApiError();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());
        return response;
    }

}
