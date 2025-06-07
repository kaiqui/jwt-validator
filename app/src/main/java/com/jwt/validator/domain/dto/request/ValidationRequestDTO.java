package com.jwt.validator.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ValidationRequestDTO(
        @NotBlank(message = "JWT cannot be blank")
        String token
) {}
