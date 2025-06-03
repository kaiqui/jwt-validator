package com.jwt.validator.service.jwt;

public interface JwtValidationService {
    boolean validateJwt(String token);
}