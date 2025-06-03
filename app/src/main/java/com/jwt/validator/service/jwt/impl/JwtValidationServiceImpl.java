package com.jwt.validator.service.jwt.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.validator.config.ValidationConstraints;
import com.jwt.validator.service.jwt.JwtValidationService;
import com.jwt.validator.service.prime.PrimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class JwtValidationServiceImpl implements JwtValidationService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PrimeService primeService;
    
    @Autowired
    public JwtValidationServiceImpl(PrimeService primeService) {
        this.primeService = primeService;
    }
    
    @Override
    public boolean validateJwt(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonNode payload = objectMapper.readTree(payloadJson);
            
            return validatePayloadStructure(payload) && 
                   validateClaims(payload);
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean validatePayloadStructure(JsonNode payload) {
        return payload.size() == 3 && 
               payload.has("Name") && 
               payload.has("Role") && 
               payload.has("Seed");
    }
    
    private boolean validateClaims(JsonNode payload) {
        return validateName(payload.get("Name").asText()) &&
               validateRole(payload.get("Role").asText()) &&
               validateSeed(payload.get("Seed").asText());
    }
    
    private boolean validateName(String name) {
        if (name == null || name.isEmpty()) return false;
        return name.length() <= ValidationConstraints.MAX_NAME_LENGTH && 
               !name.matches(".*\\d.*");
    }
    
    private boolean validateRole(String role) {
        return ValidationConstraints.ALLOWED_ROLES.contains(role);
    }
    
    private boolean validateSeed(String seed) {
        try {
            int number = Integer.parseInt(seed);
            return primeService.isPrime(number);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}