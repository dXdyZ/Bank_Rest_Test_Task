package com.example.bank_rest_test_task.dto;

import com.example.bank_rest_test_task.entity.BlockRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request to process a card block")
public class ProcessBlockRequestDto {
    @Schema(description = "ID of the block request to process", example = "1")
    @NotNull(message = "Request ID cannot be null")
    private Long requestId;

    @Schema(description = "New status for the block request")
    @NotNull(message = "Status cannot be null")
    private BlockRequestStatus status;

    @Schema(description = "ID of the admin processing the request", example = "1")
    @NotNull(message = "Admin ID cannot be null")
    private Long adminId;
}
