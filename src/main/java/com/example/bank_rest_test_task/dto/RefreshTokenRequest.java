package com.example.bank_rest_test_task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Request to refresh an access token")
public class RefreshTokenRequest {
    @Schema(description = "Refresh token", example = "def50200f496c85c9a1c...")
    @NotBlank(message = "Refresh token must be specified")
    private String refreshToken;
}