package com.jwt.validator.service.jwt.impl;

import static com.jwt.validator.utils.tracing.DataDogUtils.addTag;
import static com.jwt.validator.utils.tracing.DataDogUtils.startAndLogSpan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.validator.domain.constants.ValidationConstraints;
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
    public ResponseEntity<Boolean> validateJwt(String token) {
        log.info("Starting JWT validation process");
        log.debug("Full token received: {}", token);

        try {
            // Token null/blank check is now handled by DTO validation
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
                log.debug("Valid payload - [Name] {} [Role] {} [Seed] {}",
                        payload.path("Name").asText(),
                        payload.path("Role").asText(),
                        payload.path("Seed").asText());
                addTag(tags, "context.role", payload.path("Role").asText());
                addTag(tags, "context.seed", payload.path("Seed").asText());
                startAndLogSpan(tags);
                return ResponseEntity.ok(true);
            } else {
                log.warn("JWT validation failed");
                addTag(tags, "context.payload", payload.toString());
                startAndLogSpan(tags);
                return ResponseEntity.badRequest().body(false);
            }

        } catch (IllegalArgumentException e) {
            log.warn("Base64 decoding error: {}", e.getMessage());
            Map<String, Object> tags = new HashMap<>();
            addTag(tags, "context.invalid_cause", "Base64 decoding error");
            startAndLogSpan(tags);
            return ResponseEntity.badRequest().body(false);
        } catch (JsonProcessingException e) {
            log.warn("Invalid JSON payload: {}", e.getMessage());
            Map<String, Object> tags = new HashMap<>();
            addTag(tags, "context.invalid_cause", "Invalid JSON payload");
            startAndLogSpan(tags);
            return ResponseEntity.badRequest().body(false);
        } catch (Exception e) {
            log.error("Unexpected exception during JWT validation: {}", e.getMessage());
            Map<String, Object> tags = new HashMap<>();
            addTag(tags, "context.error", e.getClass().getSimpleName());
            startAndLogSpan(tags);
            return ResponseEntity.badRequest().body(false);
        } finally {
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
            log.warn("Role validation failed. Allowed roles", ValidationConstraints.ALLOWED_ROLES);
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
            log.warn("Name exceeds maximum length",
                    String.valueOf(name.length()), String.valueOf(ValidationConstraints.MAX_NAME_LENGTH));
            return false;
        }

        if (name.matches(".*\\d.*")) {
            log.warn("Name contains numbers", name);
            return false;
        }

        return true;
    }

    private boolean validateRole(String role) {
        boolean isValid = ValidationConstraints.ALLOWED_ROLES.contains(role);
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