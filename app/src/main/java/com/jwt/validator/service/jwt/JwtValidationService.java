package com.jwt.validator.service.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface JwtValidationService {
    ResponseEntity<Boolean> validateJwt(String token) throws JsonProcessingException;
}