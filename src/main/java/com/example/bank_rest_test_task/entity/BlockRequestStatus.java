package com.example.bank_rest_test_task.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Block request status")
public enum BlockRequestStatus {
    PENDING, APPROVED, REJECTED
}
