package com.jwt.validator.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.jwt.validator.service.jwt.JwtValidationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/validate")
public class ValidationController {
    
    private final JwtValidationService validationService;
    
    @Autowired
    public ValidationController(JwtValidationService validationService) {
        this.validationService = validationService;
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
        boolean isValid = validationService.validateJwt(request.jwt());
        HttpStatus status = isValid ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(isValid);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.isEmpty() ? 
            "Invalid request format" : 
            "Invalid request format: " + fieldErrors.get(0).getDefaultMessage();
        
        return ResponseEntity.badRequest().body(errorMessage);
    }
        
    public record ValidationRequest(
        @NotBlank(message = "JWT cannot be blank")
        String jwt
    ) {}
}