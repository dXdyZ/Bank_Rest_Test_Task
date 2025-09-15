package com.example.bank_rest_test_task.dto;

import com.example.bank_rest_test_task.entity.StatusCard;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update a card status")
public record UpdateStatusCardDto (
        @Schema(description = "New status for the card")
        StatusCard newStatus
){}