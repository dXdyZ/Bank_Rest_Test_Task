package com.example.bank_rest_test_task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to register a new user")
public record UserRegisterDto(
        @Schema(description = "Username for the new user", example = "new_user")
        @NotNull(message = "Username must be specified")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 100 characters long")
        String username,
        @Schema(description = "Password for the new user", example = "password123")
        @NotNull(message = "Password must be specified")
        @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters long")
        String password
) {
}