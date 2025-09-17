package com.example.bank_rest_test_task.dto;

import com.example.bank_rest_test_task.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User details and card details")
public class UserDto {
    @Schema(description = "User ID", example = "1")
    private Long id;
    @Schema(description = "Username", example = "john_doe")
    private String username;
    @Schema(description = "User role")
    private UserRole role;
    @Schema(description = "List of user's cards")
    private List<CardDto> cards;
    @Schema(description = "Is the user account enabled", example = "true")
    private Boolean accountEnable;
    @Schema(description = "Is the user account locked", example = "false")
    private Boolean accountLocked;
}