package com.example.bank_rest_test_task.util.factory;

import com.example.bank_rest_test_task.dto.CardDto;
import com.example.bank_rest_test_task.dto.UserDto;
import com.example.bank_rest_test_task.entity.User;
import com.example.bank_rest_test_task.util.CryptoService;
import org.springframework.stereotype.Component;

@Component
public class UserDtoFactory {
    private final CryptoService cryptoService;

    public UserDtoFactory(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public UserDto createUserDtoAndCardDtoForAdminWithCards(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .cards(user.getCards().stream().map(card ->  CardDto.builder()
                            .cardId(card.getId())
                            .statusCard(card.getStatusCard())
                            .balance(card.getBalance())
                            .validityPeriod(card.getValidityPeriod())
                            .cardNumber(cryptoService.decrypt(card.getEncryptNumber()))
                            .build()).toList())
                .accountEnable(user.getAccountEnable())
                .accountLocked(user.getAccountLocked())
                .build();
    }
    
    public UserDto createUserDtoWithoutCards(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .accountEnable(user.getAccountEnable())
                .accountLocked(user.getAccountLocked())
                .build();
    }
}
