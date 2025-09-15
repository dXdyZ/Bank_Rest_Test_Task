package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.controller.documentation.AdminCardControllerDocs;
import com.example.bank_rest_test_task.dto.*;
import com.example.bank_rest_test_task.service.CardService;
import com.example.bank_rest_test_task.util.factory.CardDtoFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/admin/cards")
public class AdminCardController implements AdminCardControllerDocs {
    private final CardService cardService;
    private final CardDtoFactory cardDtoFactory;

    public AdminCardController(CardService cardService, CardDtoFactory cardDtoFactory) {
        this.cardService = cardService;
        this.cardDtoFactory = cardDtoFactory;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCard(@Valid @RequestBody CardCreateDto cardCreateDto) {
        cardService.createCard(cardCreateDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCardById(@Positive(message = "Id must not be less than zero") @PathVariable Long id) {
        return ResponseEntity.ok(cardDtoFactory.createCardDtoForAdmin(cardService.findCardById(id)));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<PageResponse<CardDto>> getUserCards(@Positive(message = "Id must not be less than zero") @PathVariable Long id,
                                                              @PageableDefault(size = 6, sort = "validityPeriod") Pageable pageable) {
        return ResponseEntity.ok(
                PageResponse.from(cardService.getUserCardsPaginated(id, pageable).map(cardDtoFactory::createCardDtoForAdmin)));
    }

    @GetMapping("/by-number")
    public ResponseEntity<CardDto> getCardByCardNumber(@CreditCardNumber @RequestParam("number") String number) {
        return ResponseEntity.ok(cardDtoFactory.createCardDtoForAdmin(cardService.findCardByNumber(number)));
    }

    @PatchMapping("/{cardId}/status")
    public ResponseEntity<CardDto> updateStatusCard(@Positive(message = "Id must not be less than zero") @PathVariable Long cardId,
                                                    @RequestBody UpdateStatusCardDto cardDto) {
        return ResponseEntity.ok(cardDtoFactory.createCardDtoForAdmin(cardService.updateCardStatus(cardId, cardDto.newStatus())));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCardById(@Positive(message = "Id must not be less than zero") @PathVariable Long id) {
        cardService.deleteCardById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-number")
    public ResponseEntity<?> deleteCardByCardNumber(@CreditCardNumber @RequestParam("number") String number) {
        cardService.deleteCardByCardNumber(number);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-username")
    public ResponseEntity<PageResponse<CardDto>> getCardsByUserName(@NotBlank(message = "Username must be not empty") @RequestParam String username,
                                                                    @PageableDefault(size = 6, sort = "validityPeriod") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(cardService.findCardsByUsername(username, pageable).map(cardDtoFactory::createCardDtoForAdmin)));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CardDto>> getAllCards(@PageableDefault(size = 6, sort = "validityPeriod") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(cardService.findAllCards(pageable).map(cardDtoFactory::createCardDtoForAdmin)));
    }

    //TODO добавить поинт в spring security

    /**
     * Получение карт по фильтрам
     *
     * @param searchFilter объект содержащий критерии для фильтров {@link CardSearchFilter}
     * @param pageable объект пагинации
     * @return результат фильтрации
     */
    @PostMapping("/search")
    public ResponseEntity<PageResponse<CardDto>> searchCard(@RequestBody CardSearchFilter searchFilter,
                                                            @PageableDefault(size = 6, sort = "validityPeriod") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(cardService.searchCard(searchFilter, pageable)
                .map(cardDtoFactory::createCardDtoForAdmin)));
    }
}








