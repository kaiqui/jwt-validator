package com.jwt.validator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jwt.validator.dto.request.ValidationRequestDTO;
import com.jwt.validator.service.jwt.JwtValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/validate")
@RequiredArgsConstructor
public class ValidationController {

    private final JwtValidationService jwtValidationService;

    @Operation(summary = "Validate JWT token", description = "Checks if JWT meets all validation rules")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "Token is invalid or malformed",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    @GetMapping
    public ResponseEntity<Boolean> validateJwt(@Valid ValidationRequestDTO validationRequest) throws JsonProcessingException {
        return jwtValidationService.validateJwt(validationRequest.token());
    }
}
