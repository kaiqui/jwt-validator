package com.jwt.validator.exceprions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jwt.validator.utils.logs.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

import static com.jwt.validator.utils.tracing.DataDogUtils.addTag;
import static com.jwt.validator.utils.tracing.DataDogUtils.startAndLogSpan;

@ControllerAdvice(basePackages = "com.jwt.validator.controller")
public class GlobalExceptionHandler {

    private static final LogManager<GlobalExceptionHandler> log = new LogManager<>(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Boolean> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.warn("Base64 decoding error: {}", ex.getMessage());
        Map<String, Object> tags = new HashMap<>();
        addTag(tags, "context.invalid_cause", "Base64 decoding error");
        startAndLogSpan(tags);
        return ResponseEntity.badRequest().body(false);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Boolean> handleJsonProcessingException(JsonProcessingException ex, WebRequest request) {
        log.warn("Invalid JSON payload: {}", ex.getMessage());
        Map<String, Object> tags = new HashMap<>();
        addTag(tags, "context.invalid_cause", "Invalid JSON payload");
        startAndLogSpan(tags);
        return ResponseEntity.badRequest().body(false);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Boolean> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected exception: {}", ex.getMessage());
        Map<String, Object> tags = new HashMap<>();
        addTag(tags, "context.error", ex.getClass().getSimpleName());
        startAndLogSpan(tags);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
}
