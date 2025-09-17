package com.example.bank_rest_test_task.dto;

import com.example.bank_rest_test_task.entity.StatusCard;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
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

    // Проверка, что в номере карты ровно 16 цифр (если передано)
    @Pattern(regexp = "\\d{16}", message = "fullNumber must contain exactly 16 digits")
    @Schema(description = "Full number card", example = "5555555555555599")
    private String fullNumber;

    // Проверка, что в номере карты ровно 8 цифр (если передано)
    @Pattern(regexp = "\\d{8}", message = "first8Number must contain exactly 8 digits")
    @Schema(description = "The first 8 digits of the card number", example = "55555555")
    private String first8Number;

    // Проверка, что в номере карты ровно 8 цифр (если передано)
    @Pattern(regexp = "\\d{4}", message = "last4Number must contain exactly 4 digits")
    @Schema(description = "The last 4 digits of the card number", example = "5599")
    private String last4Number;

    @Schema(description = "Validity date from. Format: YYYY-MM-DD", example = "2024-01-01")
    private LocalDate validFrom;

    @Schema(description = "Validity date to. Format: YYYY-MM-DD", example = "2027-12-31")
    private LocalDate validTo;

    // Баланс не отрицательный; до 2 знаков после запятой
    @DecimalMin(value = "0.00", inclusive = true, message = "balanceMin must be >= 0.00")
    @Digits(integer = 18, fraction = 2, message = "balanceMin must have up to 2 decimal places")
    @Schema(description = "Minimum balance", example = "0.00")
    private BigDecimal balanceMin;

    // Баланс не отрицательный; до 2 знаков после запятой
    @DecimalMin(value = "0.00", inclusive = true, message = "balanceMax must be >= 0.00")
    @Digits(integer = 18, fraction = 2, message = "balanceMax must have up to 2 decimal places")
    @Schema(description = "Maximum balance", example = "1000.00")
    private BigDecimal balanceMax;

    // validFrom <= validTo (если обе заданы)
    @AssertTrue(message = "validFrom must be before or equal to validTo")
    public boolean isDateRangeValid() {
        if (validFrom == null || validTo == null) return true;
        return !validFrom.isAfter(validTo);
    }

    // balanceMin <= balanceMax (если обе заданы)
    @AssertTrue(message = "balanceMin must be less than or equal to balanceMax")
    public boolean isBalanceRangeValid() {
        if (balanceMin == null || balanceMax == null) return true;
        return balanceMin.compareTo(balanceMax) <= 0;
    }
}
