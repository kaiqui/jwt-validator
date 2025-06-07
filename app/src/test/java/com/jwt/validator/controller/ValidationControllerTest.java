package com.jwt.validator.controller;

import com.jwt.validator.domain.dto.request.ValidationRequestDTO;
import com.jwt.validator.service.jwt.JwtValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationControllerTest {

    @Mock
    private JwtValidationService jwtValidationService;

    @InjectMocks
    private ValidationController validationController;

    private final String validToken = "valid.token.here";
    private final String invalidToken = "invalid.token.here";

    private ValidationRequestDTO createRequestDto(String token) {
        return new ValidationRequestDTO(token);
    }

    @Test
    void validateJwt_WithValidToken_ShouldReturnTrue() {
        Boolean mockResponse = true;
        ResponseEntity<Boolean> mockEntity = ResponseEntity.ok(mockResponse);
        ValidationRequestDTO request = createRequestDto(validToken);

        when(jwtValidationService.validateJwt(validToken)).thenReturn(mockEntity);

        ResponseEntity<Boolean> actualResponse =
                validationController.validateJwt(request);

        assertTrue(actualResponse.getBody());
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        verify(jwtValidationService, times(1)).validateJwt(validToken);
    }

    @Test
    void validateJwt_WithInvalidToken_ShouldReturnFalse() {
        Boolean mockResponse = false;
        ResponseEntity<Boolean> mockEntity =
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockResponse);
        ValidationRequestDTO request = createRequestDto(invalidToken);

        when(jwtValidationService.validateJwt(invalidToken)).thenReturn(mockEntity);

        ResponseEntity<Boolean> actualResponse =
                validationController.validateJwt(request);

        assertFalse(actualResponse.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        verify(jwtValidationService, times(1)).validateJwt(invalidToken);
    }

    @Test
    void validateJwt_WithEmptyToken_ShouldReturnBadRequest() {
        String emptyToken = "";
        ValidationRequestDTO request = createRequestDto(emptyToken);

        Boolean mockResponse = false;
        ResponseEntity<Boolean> mockEntity =
                ResponseEntity.badRequest().body(mockResponse);

        when(jwtValidationService.validateJwt(emptyToken)).thenReturn(mockEntity);

        ResponseEntity<Boolean> actualResponse =
                validationController.validateJwt(request);

        assertFalse(actualResponse.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        verify(jwtValidationService, times(1)).validateJwt(emptyToken);
    }

    @Test
    void validateJwt_WithNullToken_ShouldThrowException() {
        ValidationRequestDTO request = createRequestDto(null);

        when(jwtValidationService.validateJwt(null))
                .thenThrow(new IllegalArgumentException("Token cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            validationController.validateJwt(request);
        });
        verify(jwtValidationService, times(1)).validateJwt(null);
    }
}