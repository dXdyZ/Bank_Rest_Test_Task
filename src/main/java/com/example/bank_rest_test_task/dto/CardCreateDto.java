package com.example.bank_rest_test_task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.time.LocalDate;

@Schema(description = "Data for creating a new card")
public record CardCreateDto(
        @Schema(description = "User ID of the card holder", example = "1")
        @NotNull(message = "User id must not be empty")
        @Positive(message = "Id must not be less than zero")
        Long userId,
        @Schema(description = "Card number", example = "4000123456789010")
        @NotNull(message = "Card number must be specified")
        @CreditCardNumber(message = "Incorrect card number format")
        String cardNumber,
        @Schema(description = "Expiration date of the card", example = "2028-12-31")
        @NotNull(message = "Validity period of the card must be specified")
        LocalDate validityPeriod
) {
}