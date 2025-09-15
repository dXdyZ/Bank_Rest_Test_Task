package com.example.bank_rest_test_task.dto;

import com.example.bank_rest_test_task.entity.StatusCard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Builder
@Schema(description = "Data for card filter")
public class CardSearchFilter {
    @Schema(description = "Id owner card", example = "1")
    private Long userId;
    @Schema(description = "Current status of the card", implementation = StatusCard.class, example = "ACTIVE")
    private StatusCard status;
    @Schema(description = "Full number card", example = "5555555555555599")
    private String fullNumber;
    @Schema(description = "The first 8 digits of the card number", example = "55555555")
    private String first8Number;
    @Schema(description = "The last 4 digits of the card number", example = "5599")
    private String last4Number;
    @Schema(description = "Validity date from. Format: YYYY-MM-DD", example = "2024-01-01")
    private LocalDate validFrom;
    @Schema(description = "Validity date to. Format: YYYY-MM-DD", example = "2027-12-31")
    private LocalDate validTo;
    @Schema(description = "Minimum balance", example = "0.00")
    private BigDecimal balanceMin;
    @Schema(description = "Maximum balance", example = "1000.00")
    private BigDecimal balanceMax;
}
