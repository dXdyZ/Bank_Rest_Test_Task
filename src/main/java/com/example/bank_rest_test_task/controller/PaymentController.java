package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.controller.documentation.PaymentControllerDocs;
import com.example.bank_rest_test_task.dto.CardDto;
import com.example.bank_rest_test_task.dto.PaymentDto;
import com.example.bank_rest_test_task.service.PaymentService;
import com.example.bank_rest_test_task.util.factory.CardDtoFactory;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.bank_rest_test_task.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController implements PaymentControllerDocs {
    private final PaymentService paymentService;
    private final CardDtoFactory cardDtoFactory;

    public PaymentController(PaymentService paymentService, CardDtoFactory cardDtoFactory) {
        this.paymentService = paymentService;
        this.cardDtoFactory = cardDtoFactory;
    }

    @PostMapping
    public ResponseEntity<List<CardDto>> transferMoney(@AuthenticationPrincipal Jwt jwt,
                                                       @Valid @RequestBody PaymentDto paymentDto) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(paymentService.transferMoney(paymentDto, userId).stream()
                .map(cardDtoFactory::createCardDtoForUser).toList());
    }
}
