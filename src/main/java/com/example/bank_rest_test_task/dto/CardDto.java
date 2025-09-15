package com.example.bank_rest_test_task.dto;

import com.example.bank_rest_test_task.entity.StatusCard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Card details")
public class CardDto {
    @Schema(description = "Card ID", example = "1")
    private Long cardId;
    @Schema(description = "Masked card number", example = "**** **** **** 9010")
    private String cardNumber;
    @Schema(description = "Expiration date of the card", example = "2028-12-31")
    private LocalDate validityPeriod;
    @Schema(description = "Current status of the card")
    private StatusCard statusCard;
    @Schema(description = "Current balance of the card", example = "1000.00")
    private BigDecimal balance;
}