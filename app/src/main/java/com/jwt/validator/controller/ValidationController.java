package com.jwt.validator.controller;

import com.jwt.validator.service.jwt.JwtValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/validate")
@Slf4j
public class ValidationController {

    private final JwtValidationService validationService;

    @Autowired
    public ValidationController(JwtValidationService validationService) {
        this.validationService = validationService;
        log.debug("ValidationController initialized with JwtValidationService");
    }

    @Operation(summary = "Validate JWT token", description = "Checks if JWT meets all validation rules")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "Token is invalid or malformed",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    @PostMapping
    public ResponseEntity<Boolean> validateJwt(
            @Parameter(description = "JWT token to validate", required = true)
            @RequestBody @Valid ValidationRequest request
    ) {
        MDC.put("operation", "validateJwt");

        log.info("Starting JWT validation");
        log.debug("Full JWT received: {}", request.jwt());

        try {
            boolean isValid = validationService.validateJwt(request.jwt());

            if (isValid) {
                log.info("JWT validation successful");
            } else {
                log.warn("JWT validation failed - invalid token");
            }

            HttpStatus status = isValid ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(isValid);
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation", e);
            throw e;
        } finally {
            MDC.clear();
            log.debug("Cleared MDC context");
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        MDC.put("operation", "handleValidationExceptions");

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.isEmpty() ?
                "Invalid request format" :
                "Invalid request format: " + fieldErrors.get(0).getDefaultMessage();

        log.warn("Validation error: {}", errorMessage);
        log.debug("Full validation errors: {}", fieldErrors);

        MDC.clear();
        return ResponseEntity.badRequest().body(errorMessage);
    }

    public record ValidationRequest(
            @NotBlank(message = "JWT cannot be blank")
            String jwt
    ) {}
}
