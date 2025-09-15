package com.example.bank_rest_test_task.exception;

public class CardDuplicateException extends RuntimeException {
    public CardDuplicateException(String message) {
        super(message);
    }
}
