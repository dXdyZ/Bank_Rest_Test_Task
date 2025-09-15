package com.example.bank_rest_test_task.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Request for a payment transfer")
public record PaymentDto(
        @Schema(description = "ID of the card to transfer from", example = "1")
        @NotNull(message = "Card from which the funds are being withdrawn must be indicated")
        Long fromCardId,
        @Schema(description = "ID of the card to transfer to", example = "2")
        @NotNull(message = "Card to which the funds are credited must be indicated")
        Long toCardId,
        @Schema(description = "Amount to transfer", example = "100.50")
        @Digits(integer = 15, fraction = 4)
        @Positive(message = "Amount must not be less than zero")
        BigDecimal amount,
        @Schema(description = "Comment for the payment", example = "Payment for goods")
        String comment
) {
}