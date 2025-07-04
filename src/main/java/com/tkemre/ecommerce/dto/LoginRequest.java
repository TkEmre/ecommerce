package com.tkemre.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
        @NotBlank(message = "Username cannot be blank")
        @Email(message = "Username must be a valid email format")
        String username,

        @NotBlank(message = "Password cannot be blank")
        String password
) {}