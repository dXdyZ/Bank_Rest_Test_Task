package com.example.bank_rest_test_task.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for user authentication")
public record AuthRequestDto(
        @Schema(description = "User's login name", example = "john_doe")
        @NotBlank(message = "Username must not be empty")
        String username,
        @Schema(description = "User's password", example = "password123")
        @NotBlank(message = "Password must not be empty")
        String password
) {}