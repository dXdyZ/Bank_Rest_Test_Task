package com.example.bank_rest_test_task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to update a username")
public record UsernameUpdateDto(
        @Schema(description = "New username", example = "john_doe_new")
        @NotBlank(message = "New username must not be empty")
        @Size(min = 3, max = 50, message = "Username must be in the range of 3 to 50 characters")
        String newUsername
) {
}