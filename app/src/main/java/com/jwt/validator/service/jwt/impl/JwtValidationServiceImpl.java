package com.jwt.validator.service.jwt.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.validator.constants.ValidationConstraints;
import com.jwt.validator.service.jwt.JwtValidationService;
import com.jwt.validator.service.prime.PrimeService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
        log.debug("JwtValidationServiceImpl initialized with PrimeService");
    }

    @Override
    public boolean validateJwt(String token) {
        MDC.put("operation", "jwt-validation");

        log.info("Starting JWT validation process");
        log.debug("Full token received: {}", token);

        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.warn("Invalid JWT structure - expected 3 parts, got {}", parts.length);
                return false;
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            log.debug("Decoded payload: {}", payloadJson);

            JsonNode payload = objectMapper.readTree(payloadJson);
            boolean isValid = validatePayloadStructure(payload) && validateClaims(payload);

            if (isValid) {
                log.info("JWT validation successful");
                log.debug("Valid payload details - Name: {}, Role: {}, Seed: {}",
                        payload.get("Name"), payload.get("Role"), payload.get("Seed"));
            } else {
                log.warn("JWT validation failed");
            }

            return isValid;
        } catch (Exception e) {
            log.error("Exception during JWT validation", e);
            return false;
        } finally {
            MDC.clear();
            log.debug("JWT validation process completed");
        }
    }

    private boolean validatePayloadStructure(JsonNode payload) {
        boolean isValid = payload.size() == 3 &&
                payload.has("Name") &&
                payload.has("Role") &&
                payload.has("Seed");

        if (!isValid) {
            log.warn("Invalid payload structure. Expected fields: Name, Role, Seed");
            log.debug("Actual payload fields: {}", payload.fieldNames());
        }

        return isValid;
    }

    private boolean validateClaims(JsonNode payload) {
        if (!validateName(payload.get("Name").asText())) {
            log.warn("Name validation failed");
            return false;
        }

        if (!validateRole(payload.get("Role").asText())) {
            log.warn("Role validation failed. Allowed roles: {}", ValidationConstraints.ALLOWED_ROLES);
            return false;
        }

        if (!validateSeed(payload.get("Seed").asText())) {
            log.warn("Seed validation failed");
            return false;
        }

        return true;
    }

    private boolean validateName(String name) {
        if (name == null || name.isEmpty()) {
            log.warn("Name is null or empty");
            return false;
        }

        if (name.length() > ValidationConstraints.MAX_NAME_LENGTH) {
            log.warn("Name exceeds maximum length ({} > {})",
                    name.length(), ValidationConstraints.MAX_NAME_LENGTH);
            return false;
        }

        if (name.matches(".*\\d.*")) {
            log.warn("Name contains numbers: {}", name);
            return false;
        }

        return true;
    }

    private boolean validateRole(String role) {
        boolean isValid = ValidationConstraints.ALLOWED_ROLES.contains(role);
        if (!isValid) {
            log.warn("Invalid role: {}. Allowed roles: {}", role, ValidationConstraints.ALLOWED_ROLES);
        }
        return isValid;
    }

    private boolean validateSeed(String seed) {
        try {
            int number = Integer.parseInt(seed);
            boolean isPrime = primeService.isPrime(number);

            if (!isPrime) {
                log.warn("Seed is not a prime number: {}", number);
            }

            return isPrime;
        } catch (NumberFormatException e) {
            log.warn("Invalid seed format: {}", seed);
            return false;
        }
    }
}