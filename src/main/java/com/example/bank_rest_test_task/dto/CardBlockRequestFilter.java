package com.example.bank_rest_test_task.dto;

import com.example.bank_rest_test_task.entity.BlockRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;


@Data
@Builder
@Schema(description = "Filters for searching card block requests. Non-null fields are combined with AND.")
public class CardBlockRequestFilter {
    @Schema(description = "Request status to match", implementation = BlockRequestStatus.class, example = "APPROVED")
    private BlockRequestStatus status;
    @Schema(description = "Processed flag: true — only processed, false — only pending. Omit to include both.", example = "true")
    private Boolean processed;
    @Schema(description = "Requester user ID", example = "42")
    private Long requesterId;
    @Schema(description = "Requester username contains", example = "john")
    private String requesterUsername;
    @Schema(description = "Processor user ID. Exact match", example = "7")
    private Long processedById;
    @Schema(description = "Reason text contains", example = "lost card")
    private String reasonContains;
    @Schema(description = "Created from", example = "2025-01-01T00:00:00Z")
    private OffsetDateTime createdFrom;
    @Schema(description = "Created to", example = "2025-01-31T23:59:59Z")
    private OffsetDateTime createdTo;
    @Schema(description = "Processed from", example = "2025-02-01T00:00:00Z")
    private OffsetDateTime processedFrom;
    @Schema(description = "Processed to", example = "2025-02-28T23:59:59Z")
    private OffsetDateTime processedTo;
}