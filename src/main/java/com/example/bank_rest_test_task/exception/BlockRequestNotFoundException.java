package com.example.bank_rest_test_task.exception;

public class BlockRequestNotFoundException extends RuntimeException {
    public BlockRequestNotFoundException(String message) {
        super(message);
    }
}
