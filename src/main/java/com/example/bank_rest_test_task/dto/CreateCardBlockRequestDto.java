package com.example.bank_rest_test_task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create a card block")
public record CreateCardBlockRequestDto(
        @Schema(description = "ID of the card to be blocked", example = "1")
        @Positive(message = "Id must not be less than zero")
        Long cardId,
        @Schema(description = "Reason for blocking the card", example = "I lost my card")
        @NotBlank(message = "Reason should not be empty")
        @Size(min = 5, max = 255, message = "Reason description should be in the range of 5 to 255 characters")
        String reason
) {}