package com.example.bank_rest_test_task.service;

import com.example.bank_rest_test_task.dto.CardBlockRequestFilter;
import com.example.bank_rest_test_task.dto.CreateCardBlockRequestDto;
import com.example.bank_rest_test_task.entity.*;
import com.example.bank_rest_test_task.exception.BlockRequestNotFoundException;
import com.example.bank_rest_test_task.exception.CardBlockedException;
import com.example.bank_rest_test_task.exception.CardNotFoundException;
import com.example.bank_rest_test_task.repository.CardBlockRequestRepository;
import com.example.bank_rest_test_task.repository.CardBlockRequestSpecifications;
import com.example.bank_rest_test_task.util.LogMarker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;


/**
 * Сервис управления заявками на блокировку карт.
 *
 * Операции:
 * - создание заявки и перевод карты в статус ожидания блокировки;
 * - обработка (подтверждение / отклонение) с фиксацией обработчика и времени;
 * - фильтрация и поиск заявок.
 *
 * После одобрения карта блокируется, при отклонении возвращается в активный статус.
 */
@Slf4j
@Service
public class CardBlockRequestService {
    private final CardBlockRequestRepository cardBlockRequestRepository;
    private final UserService userService;
    private final CardService cardService;

    public CardBlockRequestService(CardBlockRequestRepository cardBlockRequestRepository,
                                   UserService userService, CardService cardService) {
        this.cardBlockRequestRepository = cardBlockRequestRepository;
        this.userService = userService;
        this.cardService = cardService;
    }

    /**
     * Создает заявку на блокировку карты
     *
     * @param createCardBlockRequestDto хранит id карты и причину
     * @param userId пользователя, который создал заявку
     * @throws CardNotFoundException если карты с таким id не существует
     * @throws CardBlockedException если карта и так уже заблокирована
     */
    @Transactional
    public void createBlockRequest(CreateCardBlockRequestDto createCardBlockRequestDto, Long userId)
            throws CardNotFoundException {

        Card card = cardService.findCardByUserIdAndCardId(createCardBlockRequestDto.cardId(), userId);

        if (card.getStatusCard() == StatusCard.BLOCKED) {
            log.warn(LogMarker.AUDIT.getMarker(), "action=CREATE_BLOCK_REQUEST | result=FAILURE | reason=CARD_BLOCK | userId={} | cardId={}",
                    userId, card.getId());
            throw new CardBlockedException("You cannot block an inactive card");
        }

        cardBlockRequestRepository.save(
                CardBlockRequest.builder()
                        .card(card)
                        .requester(card.getUser())
                        .reason(createCardBlockRequestDto.reason())
                        .build()
        );

        log.info(LogMarker.AUDIT.getMarker(), "action=CREATE_BLOCK_REQUEST | result=SUCCESSFULLY | reason=- | userId={} | cardId={}",
                userId, card.getId());

        cardService.updateCardStatus(createCardBlockRequestDto.cardId(), StatusCard.PENDING_BLOCKED);
    }

    /**
     * Обработка заявки администратором
     *
     * @param requestId id созданной заявки
     * @param status статус, который задает администратор
     * @param adminId пользователя, который обрабатывает заявку
     * @throws BlockRequestNotFoundException если заявки с таким id не существует
     */
    @Transactional
    public void processBlockRequest(Long requestId, BlockRequestStatus status, Long adminId) {
        CardBlockRequest request = cardBlockRequestRepository.findById(requestId).orElseThrow(
                () -> new BlockRequestNotFoundException("Request to block by id: %s not found".formatted(requestId)));

        User admin = userService.findUserById(adminId);

        if (status == BlockRequestStatus.APPROVED) {
            cardService.updateCardStatus(request.getCard().getId(), StatusCard.BLOCKED);
        }
        if (status == BlockRequestStatus.REJECTED) {
            cardService.updateCardStatus(request.getCard().getId(), StatusCard.ACTIVE);
        }

        request.setProcessedBy(admin);
        request.setBlockRequestStatus(status);
        request.setProcessedAt(OffsetDateTime.now());

        cardBlockRequestRepository.save(request);

        log.info(LogMarker.AUDIT.getMarker(), "action=PROCESS_BLOCK | result=SUCCESSFULLY | reason=- | requestId={} | adminId={}",
                requestId, adminId);
    }


    /**
     * Получение заявки по id
     *
     * @param id заявки
     * @return данные заявки
     */
    public CardBlockRequest findCardBlockRequestById(Long id) {
        return cardBlockRequestRepository.findById(id).orElseThrow(
                () -> new BlockRequestNotFoundException("Request for blocking by id: %s not found".formatted(id)));
    }

    /**
     * Получение всех обработанных или взятых в работу заявок указанного пользователя по id
     *
     * @param processedId id пользователя у которого идет выборка
     * @param pageable объект постраничного запроса
     * @return заявки разделенные на страницы
     */
    public Page<CardBlockRequest> findCardBlockRequestByProcessed(Long processedId, Pageable pageable) {
        return cardBlockRequestRepository.findAllByProcessedBy_Id(processedId, pageable);
    }

    /**
     * Получает заявки на блокировку по фильтрам
     *
     * @param filter данные для фильтрации; {@link CardBlockRequestFilter}
     * @param pageable объект пагинации
     * @return результат поиска по фильтрам
     */
    public Page<CardBlockRequest> searCardBlockRequest(CardBlockRequestFilter filter, Pageable pageable) {
        return cardBlockRequestRepository.findAll(CardBlockRequestSpecifications.withFilter(filter), pageable);
    }
}
