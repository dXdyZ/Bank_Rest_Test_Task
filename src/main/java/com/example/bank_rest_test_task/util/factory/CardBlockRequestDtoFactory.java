package com.example.bank_rest_test_task.util.factory;

import com.example.bank_rest_test_task.dto.CardBlockRequestDto;
import com.example.bank_rest_test_task.entity.CardBlockRequest;
import org.springframework.stereotype.Component;

@Component
public class CardBlockRequestDtoFactory {
    private final CardDtoFactory cardDtoFactory;
    private final UserDtoFactory userDtoFactory;

    public CardBlockRequestDtoFactory(CardDtoFactory cardDtoFactory, UserDtoFactory userDtoFactory) {
        this.cardDtoFactory = cardDtoFactory;
        this.userDtoFactory = userDtoFactory;
    }

    public CardBlockRequestDto creatCardBlockRequestDto(CardBlockRequest cardBlockRequest) {
        return CardBlockRequestDto.builder()
                .id(cardBlockRequest.getId())
                .card(cardDtoFactory.createCardDtoForAdmin(cardBlockRequest.getCard()))
                .requester(cardBlockRequest.getRequester() != null ?
                        userDtoFactory.createUserDtoWithoutCards(cardBlockRequest.getRequester()) : null)
                .reason(cardBlockRequest.getReason())
                .processedBy(cardBlockRequest.getProcessedBy() != null ?
                        userDtoFactory.createUserDtoWithoutCards(cardBlockRequest.getProcessedBy()) : null)
                .processedAt(cardBlockRequest.getProcessedAt())
                .blockRequestStatus(cardBlockRequest.getBlockRequestStatus())
                .createAt(cardBlockRequest.getCreateAt())
                .build();
    }
}
