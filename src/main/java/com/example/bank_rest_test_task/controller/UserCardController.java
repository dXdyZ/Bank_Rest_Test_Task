package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.controller.documentation.UserCardControllerDocs;
import com.example.bank_rest_test_task.dto.CardDto;
import com.example.bank_rest_test_task.dto.PageResponse;
import com.example.bank_rest_test_task.security.CustomUserDetails;
import com.example.bank_rest_test_task.service.CardService;
import com.example.bank_rest_test_task.util.factory.CardDtoFactory;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/cards")
public class UserCardController implements UserCardControllerDocs {
    private final CardService cardService;
    private final CardDtoFactory cardDtoFactory;

    public UserCardController(CardService cardService, CardDtoFactory cardDtoFactory) {
        this.cardService = cardService;
        this.cardDtoFactory = cardDtoFactory;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCardById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(cardDtoFactory
                .createCardDtoForUser(cardService.findCardByUserIdAndCardId(id, userId)));
    }

    @GetMapping()
    public ResponseEntity<PageResponse<CardDto>> getUserCards(@AuthenticationPrincipal Jwt jwt,
                                                              @PageableDefault(size = 6, sort = "balance") Pageable pageable) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(
                PageResponse.from(cardService.getUserCardsPaginated(userId, pageable).map(cardDtoFactory::createCardDtoForUser)));
    }
}








