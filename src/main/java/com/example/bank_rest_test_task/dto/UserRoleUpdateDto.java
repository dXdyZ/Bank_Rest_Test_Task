package com.example.bank_rest_test_task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to update a user role")
public record UserRoleUpdateDto(
        @Schema(description = "New role for the user", example = "ADMIN")
        @NotBlank(message = "Role name must not be empty")
        @Pattern(regexp = "USER|ADMIN", message = "Role must be USER or ADMIN")
        String role
) {}