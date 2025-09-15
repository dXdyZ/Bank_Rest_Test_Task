package com.example.bank_rest_test_task.util.factory;

import com.example.bank_rest_test_task.dto.CardDto;
import com.example.bank_rest_test_task.entity.Card;
import com.example.bank_rest_test_task.util.CardFormattedService;
import com.example.bank_rest_test_task.util.CryptoService;
import org.springframework.stereotype.Component;

@Component
public class CardDtoFactory {
    private final CryptoService cryptoService;

    public CardDtoFactory(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public CardDto createCardDtoForAdmin(Card card) {
        return CardDto.builder()
                .cardId(card.getId())
                .cardNumber(cryptoService.decrypt(card.getEncryptNumber()))
                .validityPeriod(card.getValidityPeriod())
                .balance(card.getBalance())
                .statusCard(card.getStatusCard())
                .build();
    }

    public CardDto createCardDtoForUser(Card card) {
        return CardDto.builder()
                .cardId(card.getId())
                .cardNumber(CardFormattedService.formatedMaskedCard(cryptoService.decrypt(card.getEncryptNumber())))
                .validityPeriod(card.getValidityPeriod())
                .balance(card.getBalance())
                .statusCard(card.getStatusCard())
                .build();
    }
}
