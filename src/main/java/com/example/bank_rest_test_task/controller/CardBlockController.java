package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.controller.documentation.CardBlockControllerDocs;
import com.example.bank_rest_test_task.dto.*;
import com.example.bank_rest_test_task.entity.BlockRequestStatus;
import com.example.bank_rest_test_task.service.CardBlockRequestService;
import com.example.bank_rest_test_task.util.factory.CardBlockRequestDtoFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@Validated
@RestController
@RequestMapping("/blocks")
public class CardBlockController implements CardBlockControllerDocs {
    private final CardBlockRequestService blockService;
    private final CardBlockRequestDtoFactory cardBlockRequestDtoFactory;

    public CardBlockController(CardBlockRequestService blockService, CardBlockRequestDtoFactory cardBlockRequestDtoFactory) {
        this.blockService = blockService;
        this.cardBlockRequestDtoFactory = cardBlockRequestDtoFactory;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBlockRequest(@AuthenticationPrincipal Jwt jwt,
                                   @Valid @RequestBody CreateCardBlockRequestDto cardBlockDto) {
        Long userId = Long.valueOf(jwt.getSubject());

        blockService.createBlockRequest(cardBlockDto, userId);
    }

    @PutMapping("/process")
    public ResponseEntity<?> processBlockRequest(@Valid @RequestBody ProcessBlockRequestDto requestDto) {
        blockService.processBlockRequest(
                requestDto.getRequestId(),
                requestDto.getStatus(),
                requestDto.getAdminId()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/processed-by/{adminId}")
    public ResponseEntity<PageResponse<CardBlockRequestDto>> findCardBlockRequestByProcessed(
            @PathVariable Long adminId,
            @PageableDefault(size = 6, sort = "createAt")  Pageable pageable
    ) {
        Page<CardBlockRequestDto> requests = blockService.findCardBlockRequestByProcessed(adminId, pageable)
                .map(cardBlockRequestDtoFactory::creatCardBlockRequestDto);
        return ResponseEntity.ok(PageResponse.from(requests));
    }


    @PostMapping("/search")
    public ResponseEntity<PageResponse<CardBlockRequestDto>> searchCardBlockRequest(
            @Valid @RequestBody CardBlockRequestFilter filter,
            @PageableDefault(size = 6, sort = "createAr") Pageable pageable) {
        Page<CardBlockRequestDto> request = blockService.searCardBlockRequest(filter, pageable)
                .map(cardBlockRequestDtoFactory::creatCardBlockRequestDto);
        return ResponseEntity.ok(PageResponse.from(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardBlockRequestDto> findCardBlockRequestById(@Positive(message = "Id must be greater than zero")
                                                                         @PathVariable Long id) {
        return ResponseEntity.ok(
                cardBlockRequestDtoFactory.creatCardBlockRequestDto(blockService.findCardBlockRequestById(id)));
    }
}
