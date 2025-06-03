package com.jwt.validator.controller;

import com.jwt.validator.service.jwt.JwtValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationControllerTest {

    @Mock
    private JwtValidationService validationService;

    @InjectMocks
    private ValidationController validationController;

    @Test
    void validateJwt_validToken_shouldReturn200WithTrue() {
        
        String token = "valid.token";
        ValidationController.ValidationRequest request = new ValidationController.ValidationRequest(token);
        when(validationService.validateJwt(token)).thenReturn(true);

        
        ResponseEntity<Boolean> response = validationController.validateJwt(request);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(validationService).validateJwt(token);
    }

    @Test
    void validateJwt_invalidToken_shouldReturn400WithFalse() {
        
        String token = "invalid.token";
        ValidationController.ValidationRequest request = new ValidationController.ValidationRequest(token);
        when(validationService.validateJwt(token)).thenReturn(false);

        
        ResponseEntity<Boolean> response = validationController.validateJwt(request);

        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody());
        verify(validationService).validateJwt(token);
    }

    @Test
    void handleValidationExceptions_shouldReturnBadRequest() {
        
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "default message");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        
        ResponseEntity<String> response = validationController.handleValidationExceptions(ex);

        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request format: default message", response.getBody());
    }

    @Test
    void handleValidationExceptions_noFieldErrors_shouldReturnGenericMessage() {
        
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        
        ResponseEntity<String> response = validationController.handleValidationExceptions(ex);

        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request format", response.getBody());
    }
}