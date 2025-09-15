package com.example.bank_rest_test_task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.bind.validation.ValidationErrors;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Standard error response")
public class ErrorResponse {
    @Schema(description = "Timestamp of the error")
    private OffsetDateTime timestamp;
    @Schema(description = "Error message", example = "Validation failed")
    private String message;
    @Schema(description = "HTTP status code", example = "400")
    private Integer code;
    @Schema(description = "List of validation errors")
    private List<ValidationError> validationErrors;


    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "Details of a validation error")
    public static class ValidationError {
        @Schema(description = "Field where the error occurred", example = "username")
        private String field;
        @Schema(description = "Error message for the field", example = "Username must not be empty")
        private String message;
    }
}