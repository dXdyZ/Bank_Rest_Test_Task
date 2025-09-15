package com.example.bank_rest_test_task.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status card")
public enum StatusCard {
    ACTIVE, BLOCKED, EXPIRED, PENDING_BLOCKED
}
