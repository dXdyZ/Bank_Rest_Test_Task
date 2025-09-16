package com.example.bank_rest_test_task.service;

import com.example.bank_rest_test_task.dto.CardCreateDto;
import com.example.bank_rest_test_task.entity.Card;
import com.example.bank_rest_test_task.entity.StatusCard;
import com.example.bank_rest_test_task.entity.User;
import com.example.bank_rest_test_task.exception.CardDuplicateException;
import com.example.bank_rest_test_task.repository.CardRepository;
import com.example.bank_rest_test_task.util.CryptoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @Mock
    CryptoService cryptoService;

    @Mock
    CardRepository cardRepository;

    @Mock
    UserService userService;

    @InjectMocks
    CardService cardService;

    @Test
    void createCard_WhenUserExistsAndDoesntDuplicatingCard() {
        User user = User.builder()
                .id(1L)
                .build();
        String cardNumber = "1234123412134123";
        String cryptNumber = "cryptNumber";
        String searchHash = "cardHash";
        LocalDate validPer = LocalDate.of(2025, 1, 1);
        Long userId = 1L;
        CardCreateDto cardCreateDto = new CardCreateDto(userId, cardNumber, validPer);


        when(userService.findUserById(userId)).thenReturn(user);
        when(cryptoService.calculationCardHash(cardNumber)).thenReturn(searchHash);
        when(cryptoService.encrypt(cardNumber)).thenReturn(cryptNumber);
        when(cardRepository.existsBySearchHash(searchHash)).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(
                Card.builder()
                        .encryptNumber(cryptNumber)
                        .user(user)
                        .validityPeriod(validPer)
                        .searchHash(searchHash)
                        .statusCard(StatusCard.ACTIVE)
                        .last4("4123")
                        .first8("12341234")
                        .build()
        );


        cardService.createCard(cardCreateDto, 1L);

        verify(userService).findUserById(userId);
        verify(cardRepository).existsBySearchHash(searchHash);
        verify(cryptoService, times(2)).calculationCardHash(cardNumber);
        verify(cryptoService).encrypt(cardNumber);
        verify(cardRepository).existsBySearchHash(searchHash);
        verify(cardRepository).save(argThat(actCard ->
                actCard.getEncryptNumber().equals(cryptNumber) &&
                        actCard.getUser().getId().equals(userId) &&
                        actCard.getValidityPeriod().equals(validPer) &&
                        actCard.getSearchHash().equals(searchHash)));
    }

    @Test
    void createCard_WhenUserExistsAndDDuplicatingCard_ThenThrowCardDuplicateException() {
        User user = User.builder()
                .id(1L)
                .build();
        String cardNumber = "1234123412134123";
        String searchHash = "cardHash";
        LocalDate validPer = LocalDate.of(2025, 1, 1);
        Long userId = 1L;
        CardCreateDto cardCreateDto = new CardCreateDto(userId, cardNumber, validPer);

        when(userService.findUserById(userId)).thenReturn(user);
        when(cardRepository.existsBySearchHash(searchHash)).thenReturn(true);
        when(cryptoService.calculationCardHash(cardNumber)).thenReturn(searchHash);

        CardDuplicateException exception = assertThrows(CardDuplicateException.class,
                () -> cardService.createCard(cardCreateDto, 1L));

        assertInstanceOf(CardDuplicateException.class, exception);
        assertEquals("Card by number: %s already exists".formatted(cardNumber), exception.getMessage());
        verify(cardRepository, never()).save(any());
        verify(cryptoService, never()).encrypt(any());
    }


}








