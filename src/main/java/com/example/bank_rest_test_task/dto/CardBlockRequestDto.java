package com.example.bank_rest_test_task.dto;

import com.example.bank_rest_test_task.entity.BlockRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Card block request details")
public class CardBlockRequestDto {
    @Schema(description = "Request ID", example = "1")
    private Long id;
    @Schema(description = "Card information")
    private CardDto card;
    @Schema(description = "User who requested the block")
    private UserDto requester;
    @Schema(description = "Reason for blocking", example = "Card lost")
    private String reason;
    @Schema(description = "Administrator who processed the request")
    private UserDto processedBy;
    @Schema(description = "Timestamp of when the request was processed")
    private OffsetDateTime processedAt;
    @Schema(description = "Status of the block request")
    private BlockRequestStatus blockRequestStatus;
    @Schema(description = "Timestamp of when the request was created")
    private OffsetDateTime createAt;
}