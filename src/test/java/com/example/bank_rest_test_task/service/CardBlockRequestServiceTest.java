package com.example.bank_rest_test_task.service;

import com.example.bank_rest_test_task.dto.CardBlockRequestFilter;
import com.example.bank_rest_test_task.dto.CreateCardBlockRequestDto;
import com.example.bank_rest_test_task.entity.*;
import com.example.bank_rest_test_task.exception.BlockRequestNotFoundException;
import com.example.bank_rest_test_task.exception.CardBlockedException;
import com.example.bank_rest_test_task.repository.CardBlockRequestRepository;
import com.example.bank_rest_test_task.util.CryptoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CardBlockRequestServiceTest {
    @Mock
    CardBlockRequestRepository cardBlockRequestRepository;
    @Mock
    CryptoService cryptoService;
    @Mock
    UserService userService;
    @Mock
    CardService cardService;

    @InjectMocks
    CardBlockRequestService cardBlockRequestService;



    @Test
    void createBlockRequest_WhenCardNoBlock() {
        Long cardId = 1L;
        Long userId = 1L;
        Card card = Card.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .build())
                .statusCard(StatusCard.ACTIVE)

                .build();

        CreateCardBlockRequestDto cardBlock = new CreateCardBlockRequestDto(cardId, "Hello test");

        when(cardService.findCardByUserIdAndCardId(cardId, userId)).thenReturn(card);

       cardBlockRequestService.createBlockRequest(cardBlock, userId);

       verify(cardBlockRequestRepository).save(argThat(actBlock ->
               actBlock.getCard().equals(card) &&
               actBlock.getRequester().getId().equals(userId) &&
               actBlock.getReason().equals("Hello test")));
       verify(cardService).updateCardStatus(cardId, StatusCard.PENDING_BLOCKED);
    }

    @Test
    void createBlockRequest_WhenCardBlock_ThenThrowCardBlockedException() {
        Long cardId = 1L;
        Long userId = 1L;
        Card card = Card.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .build())
                .statusCard(StatusCard.BLOCKED)

                .build();

        CreateCardBlockRequestDto cardBlock = new CreateCardBlockRequestDto(cardId, "Hello test");

        when(cardService.findCardByUserIdAndCardId(cardId, userId)).thenReturn(card);

        CardBlockedException exception = assertThrows(CardBlockedException.class,
                () -> cardBlockRequestService.createBlockRequest(cardBlock, userId));

        assertInstanceOf(CardBlockedException.class, exception);
        assertEquals("You cannot block an inactive card", exception.getMessage());
        verify(cardService, never()).updateCardStatus(cardId, StatusCard.PENDING_BLOCKED);
        verifyNoInteractions(cardBlockRequestRepository);
    }

    @Test
    void processBlockRequest_WhenApproved_ShouldBlockCardAndUpdateRequest() {
        Long requestId = 1L;
        Long adminId = 2L;
        Long cardId = 3L;

        Card card = Card.builder()
                .id(cardId)
                .statusCard(StatusCard.ACTIVE)
                .build();

        CardBlockRequest request = CardBlockRequest.builder()
                .id(requestId)
                .card(card)
                .blockRequestStatus(BlockRequestStatus.PENDING)
                .build();

        User admin = User.builder()
                .id(adminId)
                .build();

        when(cardBlockRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(userService.findUserById(adminId)).thenReturn(admin);

        cardBlockRequestService.processBlockRequest(requestId, BlockRequestStatus.APPROVED, adminId);

        verify(cardService).updateCardStatus(cardId, StatusCard.BLOCKED);
        assertEquals(BlockRequestStatus.APPROVED, request.getBlockRequestStatus());
        assertEquals(admin, request.getProcessedBy());
        assertNotNull(request.getProcessedAt());
    }

    @Test
    void processBlockRequest_WhenRejected_ShouldKeepCardActive() {
        Long requestId = 1L;
        Long adminId = 2L;
        Long cardId = 3L;

        Card card = Card.builder()
                .id(cardId)
                .statusCard(StatusCard.ACTIVE)
                .build();

        CardBlockRequest request = CardBlockRequest.builder()
                .id(requestId)
                .card(card)
                .blockRequestStatus(BlockRequestStatus.PENDING)
                .build();

        when(cardBlockRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(userService.findUserById(adminId)).thenReturn(new User());

        cardBlockRequestService.processBlockRequest(requestId, BlockRequestStatus.REJECTED, adminId);

        verify(cardService).updateCardStatus(cardId, StatusCard.ACTIVE);
        assertEquals(BlockRequestStatus.REJECTED, request.getBlockRequestStatus());
    }


    @Test
    void processBlockRequest_WhenRequestNotFound_ShouldThrowException() {
        Long requestId = 1L;

        when(cardBlockRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        BlockRequestNotFoundException exception = assertThrows(BlockRequestNotFoundException.class,
                () -> cardBlockRequestService.processBlockRequest(requestId, BlockRequestStatus.APPROVED, 2L));

        assertInstanceOf(BlockRequestNotFoundException.class, exception);
        assertEquals("Request to block by id: %s not found".formatted(requestId), exception.getMessage());
        verifyNoInteractions(cardService);
        verifyNoInteractions(userService);
    }

    @Test
    void searCardBlockRequest_WhenRequestExist_ShouldNotEmptyPage() {
        CardBlockRequestFilter filter = CardBlockRequestFilter.builder()
                .requesterUsername("user")
                .build();


        CardBlockRequest request = CardBlockRequest.builder()
                .id(1L)
                .requester(User.builder()
                        .id(1L)
                        .username("user")
                        .build())
                .build();

        Pageable pageable = PageRequest.of(0, 5);

        Page<CardBlockRequest> page = new PageImpl<>(
                List.of(request),
                pageable,
                1
        );

        when(cardBlockRequestRepository.findAllWithRelations(any(), any(Pageable.class))).thenReturn(page);

        Page<CardBlockRequest> result = cardBlockRequestService.searCardBlockRequest(filter, pageable);

        assertEquals(page, result);
    }

    @Test
    void searCardBlockRequest_WhenRequestDoesNotExist_ShouldEmptyPage() {
        CardBlockRequestFilter filter = CardBlockRequestFilter.builder()
                .requesterUsername("user")
                .build();

        Pageable pageable = PageRequest.of(0, 5);

        Page<CardBlockRequest> page = new PageImpl<>(
                List.of(),
                pageable,
                0
        );

        when(cardBlockRequestRepository.findAllWithRelations(any(), any(Pageable.class))).thenReturn(page);

        Page<CardBlockRequest> result = cardBlockRequestService.searCardBlockRequest(filter, pageable);

        assertEquals(page, result);
    }
}