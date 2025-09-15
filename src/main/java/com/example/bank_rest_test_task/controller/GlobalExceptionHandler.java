package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.dto.ErrorResponse;
import com.example.bank_rest_test_task.exception.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponse.ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ErrorResponse.ValidationError.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build())
                .toList();

        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message("Validation failed")
                .code(HttpStatus.BAD_REQUEST.value())
                .validationErrors(errors)
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ConstraintViolationException ex) {
        List<ErrorResponse.ValidationError> errors = ex.getConstraintViolations()
                .stream().map(constraintViolation -> ErrorResponse.ValidationError.builder()
                        .field(extractFieldName(constraintViolation))
                        .message(constraintViolation.getMessage())
                        .build())
                .toList();


        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message("Request validation failed")
                .code(HttpStatus.BAD_REQUEST.value())
                .validationErrors(errors)
                .build(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailed(AuthenticationFailedException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(ex.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .build(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJwtToken(InvalidJwtTokenException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(ex.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .build(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CardBlockedException.class)
    public ResponseEntity<ErrorResponse> handleCardBlocked(CardBlockedException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(ex.getMessage())
                .code(HttpStatus.FORBIDDEN.value())
                .build(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUser(DuplicateUserException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(ex.getMessage())
                .code(HttpStatus.CONFLICT.value())
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CardDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleCardDuplicateException(CardDuplicateException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(ex.getMessage())
                .code(HttpStatus.CONFLICT.value())
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BlockRequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBlockRequestNotFound(BlockRequestNotFoundException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCardNotFound(CardNotFoundException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAmount(InvalidAmountException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST);
    }

    private String extractFieldName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        return path.substring(path.lastIndexOf('.') + 1);
    }
}
