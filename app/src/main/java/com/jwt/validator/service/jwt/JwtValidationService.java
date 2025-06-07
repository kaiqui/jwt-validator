package com.jwt.validator.service.jwt;

import org.springframework.http.ResponseEntity;

public interface JwtValidationService {
    ResponseEntity<Boolean> validateJwt(String token);
}