package com.example.bank_rest_test_task.service;

import com.example.bank_rest_test_task.dto.PaymentDto;
import com.example.bank_rest_test_task.entity.Card;
import com.example.bank_rest_test_task.entity.StatusCard;
import com.example.bank_rest_test_task.entity.User;
import com.example.bank_rest_test_task.exception.CardBlockedException;
import com.example.bank_rest_test_task.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    TransferHistoryService transferHistoryService;

    @Mock
    CardService cardService;

    @InjectMocks
    PaymentService paymentService;

    @Test
    void transferMoney_WhenCardNoBlockNoExpiredAndEnoughMoneyOnCard() {
        Card fromCard = Card.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(300))
                .statusCard(StatusCard.ACTIVE)
                .build();
        Card toCard = Card.builder()
                .id(2L)
                .balance(BigDecimal.valueOf(200))
                .statusCard(StatusCard.ACTIVE)
                .build();
        User user = User.builder()
                .id(1L)
                .username("username")
                .cards(List.of(fromCard, toCard))
                .build();
        toCard.setUser(user);
        fromCard.setUser(user);

        PaymentDto paymentDto = new PaymentDto(fromCard.getId(), toCard.getId(), BigDecimal.valueOf(200), "Hello");

        when(cardService.findCardByUserIdAndCardId(fromCard.getId(), user.getId())).thenReturn(fromCard);
        when(cardService.findCardByUserIdAndCardId(toCard.getId(), user.getId())).thenReturn(toCard);

        List<Card> result = paymentService.transferMoney(paymentDto, 1L);

        assertNotNull(result);
        assertEquals(2, result.size());

        Card updateFromCard = result.stream()
                .filter(f -> f.getId().equals(fromCard.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(100, updateFromCard.getBalance().intValue());

        Card updateToCard = result.stream()
                .filter(f -> f.getId().equals(toCard.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(400, updateToCard.getBalance().intValue());
    }

    @Test
    void transferMoney_WhenCardNoBlockNoExpiredAndNoEnoughMoneyOnCard_ThenThrowInsufficientFundsException() {
        Card fromCard = Card.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(150))
                .statusCard(StatusCard.ACTIVE)
                .build();
        Card toCard = Card.builder()
                .id(2L)
                .balance(BigDecimal.valueOf(200))
                .statusCard(StatusCard.ACTIVE)
                .build();
        User user = User.builder()
                .id(1L)
                .username("username")
                .cards(List.of(fromCard, toCard))
                .build();
        toCard.setUser(user);
        fromCard.setUser(user);

        PaymentDto paymentDto = new PaymentDto(fromCard.getId(), toCard.getId(), BigDecimal.valueOf(200), "Hello");

        when(cardService.findCardByUserIdAndCardId(fromCard.getId(), user.getId())).thenReturn(fromCard);
        when(cardService.findCardByUserIdAndCardId(toCard.getId(), user.getId())).thenReturn(toCard);

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
                () -> paymentService.transferMoney(paymentDto, user.getId()));

        assertInstanceOf(InsufficientFundsException.class, exception);
        assertEquals("There are not enough funds on the card", exception.getMessage());
        verify(cardService, never()).saveCard(any());
        verifyNoInteractions(transferHistoryService);
    }

    @Test
    void transferMoney_WhenFromCardBlocked_ThenCardBlockedException() {
        Card fromCard = Card.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(150))
                .statusCard(StatusCard.BLOCKED)
                .build();
        Card toCard = Card.builder()
                .id(2L)
                .balance(BigDecimal.valueOf(200))
                .statusCard(StatusCard.ACTIVE)
                .build();
        User user = User.builder()
                .id(1L)
                .username("username")
                .cards(List.of(fromCard, toCard))
                .build();
        toCard.setUser(user);
        fromCard.setUser(user);

        PaymentDto paymentDto = new PaymentDto(fromCard.getId(), toCard.getId(), BigDecimal.valueOf(200), "Hello");

        when(cardService.findCardByUserIdAndCardId(fromCard.getId(), user.getId())).thenReturn(fromCard);
        when(cardService.findCardByUserIdAndCardId(toCard.getId(), user.getId())).thenReturn(toCard);

        CardBlockedException exception = assertThrows(CardBlockedException.class,
                () -> paymentService.transferMoney(paymentDto, user.getId()));

        assertInstanceOf(CardBlockedException.class, exception);
        assertEquals("Card by id: %s is blocked for operation".formatted(fromCard.getId()), exception.getMessage());
        verify(cardService, never()).saveCard(any());
        verifyNoInteractions(transferHistoryService);
    }

    @Test
    void transferMoney_WhenFromCardExpired_ThenCardBlockedException() {
        Card fromCard = Card.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(150))
                .statusCard(StatusCard.EXPIRED)
                .build();
        Card toCard = Card.builder()
                .id(2L)
                .balance(BigDecimal.valueOf(200))
                .statusCard(StatusCard.ACTIVE)
                .build();
        User user = User.builder()
                .id(1L)
                .username("username")
                .cards(List.of(fromCard, toCard))
                .build();
        toCard.setUser(user);
        fromCard.setUser(user);

        PaymentDto paymentDto = new PaymentDto(fromCard.getId(), toCard.getId(), BigDecimal.valueOf(200), "Hello");

        when(cardService.findCardByUserIdAndCardId(fromCard.getId(), user.getId())).thenReturn(fromCard);
        when(cardService.findCardByUserIdAndCardId(toCard.getId(), user.getId())).thenReturn(toCard);

        CardBlockedException exception = assertThrows(CardBlockedException.class,
                () -> paymentService.transferMoney(paymentDto, user.getId()));

        assertInstanceOf(CardBlockedException.class, exception);
        assertEquals("Card by id: %s has expired".formatted(fromCard.getId()), exception.getMessage());
        verify(cardService, never()).saveCard(any());
        verifyNoInteractions(transferHistoryService);
    }
}







