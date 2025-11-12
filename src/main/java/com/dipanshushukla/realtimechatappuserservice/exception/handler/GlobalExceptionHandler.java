package com.dipanshushukla.realtimechatappuserservice.exception.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.dipanshushukla.realtimechatappuserservice.exception.InvalidUserUpdateException;
import com.dipanshushukla.realtimechatappuserservice.exception.UserNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildBody(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);
        return body;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex) {
        log.warn("User error: {}", ex.getMessage());
        return new ResponseEntity<>(buildBody(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidUserUpdateException.class)
    public ResponseEntity<Object> handleInvalidUpdate(InvalidUserUpdateException ex) {
        log.warn("Invalid update: {}", ex.getMessage());
        return new ResponseEntity<>(buildBody(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Validation failed");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingParams(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getParameterName());

        String message = "Required parameter '" + ex.getParameterName() + "' is missing";

        return new ResponseEntity<>(
                buildBody(HttpStatus.BAD_REQUEST, message),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation", ex);

        String message = "Duplicate value violates a unique constraint";

        Throwable root = ex.getRootCause();
        if (root != null && root.getMessage() != null) {
            String lower = root.getMessage().toLowerCase();

            if (lower.contains("duplicate entry") && lower.contains("email")) {
                message = "Email already exists";
            } else if (lower.contains("duplicate entry") && lower.contains("username")) {
                message = "Username already exists";
            }
        }

        return new ResponseEntity<>(buildBody(HttpStatus.CONFLICT, message), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        return new ResponseEntity<>(buildBody(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Object> missingRequestHeaderExceptionHandler(MissingRequestHeaderException ex) {

        log.warn("Missing request header: {}", ex.getHeaderName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildBody(HttpStatus.BAD_REQUEST, ex.getMessage()));

    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {

        log.warn("Method validation error: {}", ex.getMessage());

        String message = Stream.concat(ex.getValueResults().stream(), ex.getBeanResults().stream())
                .findFirst()
                .flatMap(validationResult -> validationResult
                        .getResolvableErrors()
                        .stream()
                        .findFirst())
                .map(MessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed for request parameter.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildBody(HttpStatus.BAD_REQUEST, message));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefault(Exception ex) {
        log.error("Unexpected error:", ex);
        return new ResponseEntity<>(
                buildBody(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
