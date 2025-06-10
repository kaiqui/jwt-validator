package com.jwt.validator.service.jwt.impl;

import static com.jwt.validator.utils.tracing.DataDogUtils.addTag;
import static com.jwt.validator.utils.tracing.DataDogUtils.startAndLogSpan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.validator.constants.ValidationConstants;
import com.jwt.validator.utils.logs.LogManager;
import com.jwt.validator.service.jwt.JwtValidationService;
import com.jwt.validator.service.prime.PrimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtValidationServiceImpl implements JwtValidationService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PrimeService primeService;
    private static final LogManager<JwtValidationServiceImpl> log = new LogManager<>(JwtValidationServiceImpl.class);

    @Autowired
    public JwtValidationServiceImpl(PrimeService primeService) {
        this.primeService = primeService;
        log.debug("JwtValidationServiceImpl initialized with PrimeService");
    }

    @Override
    public ResponseEntity<Boolean> validateJwt(String token) throws JsonProcessingException {
        log.info("Starting JWT validation process");
        log.debug("Full token received: {}", token);

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            log.warn("Invalid JWT structure - expected 3 parts, got {}", parts.length);
            Map<String, Object> tags = new HashMap<>();
            addTag(tags, "context.invalid_cause", "Invalid JWT structure");
            startAndLogSpan(tags);
            return ResponseEntity.badRequest().body(false);
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        log.debug("Decoded payload: {}", payloadJson);

        JsonNode payload = objectMapper.readTree(payloadJson);
        boolean isValid = validatePayloadStructure(payload) && validateClaims(payload);

        Map<String, Object> tags = new HashMap<>();
        if (isValid) {
            log.info("JWT validation successful");
            addTag(tags, "context.role", payload.path("Role").asText());
            addTag(tags, "context.seed", payload.path("Seed").asText());
        } else {
            log.warn("JWT validation failed");
            addTag(tags, "context.payload", payload.toString());
        }
        startAndLogSpan(tags);

        return isValid ? ResponseEntity.ok(true) : ResponseEntity.badRequest().body(false);
    }

    private boolean validatePayloadStructure(JsonNode payload) {
        boolean isValid = payload.size() == 3 &&
                payload.has("Name") &&
                payload.has("Role") &&
                payload.has("Seed");

        if (!isValid) {
            log.warn("Invalid payload structure. Expected fields: Name, Role, Seed");
            log.debug("Actual payload fields", payload.fieldNames());
        }

        return isValid;
    }

    private boolean validateClaims(JsonNode payload) {
        if (!validateName(payload.get("Name").asText())) {
            log.warn("Name validation failed");
            return false;
        }

        if (!validateRole(payload.get("Role").asText())) {
            log.warn("Role validation failed. Allowed roles", ValidationConstants.ALLOWED_ROLES);
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

        if (name.length() > ValidationConstants.MAX_NAME_LENGTH) {
            log.warn("Name exceeds maximum length",
                    String.valueOf(name.length()), String.valueOf(ValidationConstants.MAX_NAME_LENGTH));
            return false;
        }

        if (name.matches(".*\\d.*")) {
            log.warn("Name contains numbers", name);
            return false;
        }

        return true;
    }

    private boolean validateRole(String role) {
        boolean isValid = ValidationConstants.ALLOWED_ROLES.contains(role);
        if (!isValid) {
            log.warn("Invalid role", role);
        }
        return isValid;
    }

    private boolean validateSeed(String seed) {
        try {
            int number = Integer.parseInt(seed);
            boolean isPrime = primeService.isPrime(number);

            if (!isPrime) {
                log.warn("Seed is not a prime number", number);
            }

            return isPrime;
        } catch (NumberFormatException e) {
            log.warn("Invalid seed format", seed);
            return false;
        }
    }
}