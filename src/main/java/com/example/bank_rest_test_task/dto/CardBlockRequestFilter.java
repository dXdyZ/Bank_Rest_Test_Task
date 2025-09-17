package com.example.bank_rest_test_task.dto;

import com.example.bank_rest_test_task.entity.BlockRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @Positive(message = "requesterId must be positive")
    private Long requesterId;

    @Schema(description = "Requester username contains", example = "john")
    @Size(min = 1, max = 50, message = "requesterUsername length must be between 1 and 50")
    private String requesterUsername;

    @Schema(description = "Processor user ID. Exact match", example = "7")
    @Positive(message = "processedById must be positive")
    private Long processedById;

    @Schema(description = "Reason text contains", example = "lost card")
    @Size(min = 1, max = 255, message = "reasonContains length must be between 1 and 255")
    private String reasonContains;

    @Schema(description = "Created from", example = "2025-01-01T00:00:00Z")
    private OffsetDateTime createdFrom;
    @Schema(description = "Created to", example = "2025-01-31T23:59:59Z")
    private OffsetDateTime createdTo;
    @Schema(description = "Processed from", example = "2025-02-01T00:00:00Z")
    private OffsetDateTime processedFrom;
    @Schema(description = "Processed to", example = "2025-02-28T23:59:59Z")
    private OffsetDateTime processedTo;

    /**
     * Проверка createdFrom <= createdTo (если обе заданы)
     *
     * @return результат проверки
     */
    @AssertTrue(message = "createdFrom must be before or equal to createdTo")
    public boolean isCreatedRangeValid() {
        if (createdFrom == null || createdTo == null) return true;
        return !createdFrom.isAfter(createdTo);
    }

    /**
     * Простая проверка: processedFrom <= processedTo (если обе заданы)
     *
     * @return результат проверки
     */
    @AssertTrue(message = "processedFrom must be before or equal to processedTo")
    public boolean isProcessedRangeValid() {
        if (processedFrom == null || processedTo == null) return true;
        return !processedFrom.isAfter(processedTo);
    }

}