package dev.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank
        String username,
        @NotBlank
        String password,
        @Email
        String email
) {}
