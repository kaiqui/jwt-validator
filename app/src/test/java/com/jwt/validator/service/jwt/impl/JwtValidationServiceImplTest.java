package com.jwt.validator.service.jwt.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.validator.service.prime.PrimeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtValidationServiceImplTest {

    @Mock
    private PrimeService primeService;

    @InjectMocks
    private JwtValidationServiceImpl jwtValidationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void validateJwt_validToken_shouldReturnTrue() {
        
        when(primeService.isPrime(7841)).thenReturn(true);
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
        
        
        boolean result = jwtValidationService.validateJwt(token);
        
        
        assertTrue(result);
        verify(primeService).isPrime(7841);
    }

    @Test
    void validateJwt_invalidStructure_shouldReturnFalse() {
        assertFalse(jwtValidationService.validateJwt("invalid.token.structure"));
        verifyNoInteractions(primeService);
    }

    @Test
    void validateJwt_invalidBase64_shouldReturnFalse() {
        assertFalse(jwtValidationService.validateJwt("header.invalidBase64.signature"));
        verifyNoInteractions(primeService);
    }

    @Test
    void validateJwt_invalidJson_shouldReturnFalse() throws JsonProcessingException {
        String invalidJson = "{invalid: json}";
        String base64Payload = Base64.getUrlEncoder().encodeToString(invalidJson.getBytes());
        String token = "header." + base64Payload + ".signature";
        
        assertFalse(jwtValidationService.validateJwt(token));
        verifyNoInteractions(primeService);
    }

    @Test
    void validateJwt_missingClaims_shouldReturnFalse() throws JsonProcessingException {
        String payload = "{\"Role\":\"Admin\",\"Name\":\"Pedro Silva\"}";
        String token = createToken(payload);
        
        assertFalse(jwtValidationService.validateJwt(token));
        verifyNoInteractions(primeService);
    }

    @Test
    void validateJwt_extraClaims_shouldReturnFalse() throws JsonProcessingException {
        String payload = "{\"Role\":\"Admin\",\"Seed\":\"7841\",\"Name\":\"Pedro Silva\",\"Extra\":\"Value\"}";
        String token = createToken(payload);
        
        assertFalse(jwtValidationService.validateJwt(token));
        verifyNoInteractions(primeService);
    }

    @Test
    void validateJwt_nameWithNumbers_shouldReturnFalse() throws JsonProcessingException {
        String payload = "{\"Role\":\"External\",\"Seed\":\"88037\",\"Name\":\"M4ria Olivia\"}";
        String token = createToken(payload);
        
        assertFalse(jwtValidationService.validateJwt(token));
        verifyNoInteractions(primeService);
    }

    @Test
    void validateJwt_nameTooLong_shouldReturnFalse() throws JsonProcessingException {
        String longName = "A".repeat(257);
        String payload = "{\"Role\":\"Admin\",\"Seed\":\"7841\",\"Name\":\"" + longName + "\"}";
        String token = createToken(payload);
        
        assertFalse(jwtValidationService.validateJwt(token));
        verifyNoInteractions(primeService);
    }

    @Test
    void validateJwt_invalidRole_shouldReturnFalse() throws JsonProcessingException {
        String payload = "{\"Role\":\"InvalidRole\",\"Seed\":\"7841\",\"Name\":\"Pedro Silva\"}";
        String token = createToken(payload);
        
        assertFalse(jwtValidationService.validateJwt(token));
        verifyNoInteractions(primeService);
    }

    @Test
    void validateJwt_seedNotPrime_shouldReturnFalse() throws JsonProcessingException {
        when(primeService.isPrime(100)).thenReturn(false);
        
        String payload = "{\"Role\":\"Admin\",\"Seed\":\"100\",\"Name\":\"Pedro Silva\"}";
        String token = createToken(payload);
        
        assertFalse(jwtValidationService.validateJwt(token));
        verify(primeService).isPrime(100);
    }

    @Test
    void validateJwt_seedNotNumber_shouldReturnFalse() throws JsonProcessingException {
        String payload = "{\"Role\":\"Admin\",\"Seed\":\"NotANumber\",\"Name\":\"Pedro Silva\"}";
        String token = createToken(payload);
        
        assertFalse(jwtValidationService.validateJwt(token));
        verifyNoInteractions(primeService);
    }

    @Test
    void validateJwt_caseSensitiveClaims_shouldReturnFalse() throws JsonProcessingException {
        String payload = "{\"role\":\"Admin\",\"seed\":\"7841\",\"name\":\"Pedro Silva\"}";
        String token = createToken(payload);
        
        assertFalse(jwtValidationService.validateJwt(token));
        verifyNoInteractions(primeService);
    }

    private String createToken(String payload) throws JsonProcessingException {
        String header = "{\"alg\":\"HS256\"}";
        String base64Header = Base64.getUrlEncoder().encodeToString(header.getBytes());
        String base64Payload = Base64.getUrlEncoder().encodeToString(payload.getBytes());
        return base64Header + "." + base64Payload + ".signature";
    }
}