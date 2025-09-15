package com.example.bank_rest_test_task.service;

import com.example.bank_rest_test_task.dto.PaymentDto;
import com.example.bank_rest_test_task.entity.Card;
import com.example.bank_rest_test_task.entity.StatusCard;
import com.example.bank_rest_test_task.entity.TransferHistory;
import com.example.bank_rest_test_task.exception.CardBlockedException;
import com.example.bank_rest_test_task.exception.InsufficientFundsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Сервис платежей (перевод средств между картами пользователя).
 *
 * Проверяет:
 * - статус и срок действия карт;
 * - достаточность средств;
 * - фиксирует перевод в истории.
 */
@Service
public class PaymentService {
    private final TransferHistoryService transferHistoryService;
    private final CardService cardService;

    /**
     * @param transferHistoryService класс для работы с историей
     * @param cardService класс для работы с картами
     */
    public PaymentService(TransferHistoryService transferHistoryService, CardService cardService) {
        this.transferHistoryService = transferHistoryService;
        this.cardService = cardService;
    }

    /**
     * Переводит деньги с одной карты на другую
     *
     * @param paymentDto содержит id двух карт сумму и комментарий к переводу
     * @param userId пользователя, который совершает перевод
     * @return обновленные данные карт
     * @throws InsufficientFundsException если не достаточно средств на карте с которой происходит перевод
     */
    @Transactional
    public List<Card> transferMoney(PaymentDto paymentDto, Long userId) {
        Card fromCard = cardService.findCardByUserIdAndCardId(paymentDto.fromCardId(), userId);
        Card toCard = cardService.findCardByUserIdAndCardId(paymentDto.toCardId(), userId);

        chekCard(fromCard, toCard);

        if (fromCard.getBalance().compareTo(paymentDto.amount()) <= 0) {
            throw new InsufficientFundsException("There are not enough funds on the card");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(paymentDto.amount()));
        toCard.setBalance(toCard.getBalance().add(paymentDto.amount()));

        transferHistoryService.saveTransferHistory(TransferHistory.builder()
                        .fromCard(fromCard)
                        .toCard(toCard)
                        .amount(paymentDto.amount())
                        .user(fromCard.getUser())
                        .comment(paymentDto.comment())
                        .timestamp(OffsetDateTime.now())
                .build());
        cardService.saveCard(fromCard);
        cardService.saveCard(toCard);
        return List.of(fromCard, toCard);
    }

    /**
     * Проверяет срок действия карт и не в заблокированы ли они
     *
     * @param fromCard карта с которой происходит списание
     * @param toCard карта на которую происходит зачисление
     * @throws CardBlockedException если карта заблокирована или истек ее срок действия
     */
    private void chekCard(Card fromCard, Card toCard) {
        if (fromCard.getStatusCard().equals(StatusCard.BLOCKED)){
            throw new CardBlockedException("Card by id: %s is blocked for operation".formatted(fromCard.getId()));
        }
        if (toCard.getStatusCard().equals(StatusCard.BLOCKED)) {
            throw new CardBlockedException("Card by id: %s is blocked for operation".formatted(toCard.getId()));
        }
        if (fromCard.getStatusCard().equals(StatusCard.EXPIRED)) {
            throw new CardBlockedException("Card by id: %s has expired".formatted(fromCard.getId()));
        }
        if (toCard.getStatusCard().equals(StatusCard.EXPIRED)) {
            throw new CardBlockedException("Card by id: %s has expired".formatted(toCard.getId()));
        }
    }
}
