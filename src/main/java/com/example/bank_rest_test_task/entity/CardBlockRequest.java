package com.example.bank_rest_test_task.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * JPA-entity, представляет заявку на блокировку карты.
 *
 * - {@link #card} - карта к которой относиться заявка; обязательно для заполнения
 * - {@link #requester} - инициатор заявки
 * - {@link #processedBy} и {@link #processedAt} - заполняются после обработки заявки, иначе null
 * - {@link #blockRequestStatus} - статус заявки; по умолчанию {@link BlockRequestStatus#APPROVED}
 * - {@link #createAt} - отметка времени создания сущности
 *
 * Особенности:
 * - Связь @ManyToOne без каскадов: связанные {@link Card} и {@link User} должны существовать в бд.
 */
@Getter
@Setter
@Entity
@Builder
@Table(name = "card_block_requests")
@NoArgsConstructor
@AllArgsConstructor
public class CardBlockRequest {

    /**
     * Уникальный идентификатор (PK), генерируется БД (IDENTITY)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Карта, для которой создается заявка на блокировку.
     * Обязательное поле (NOT NULL)
     */
    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    /**
     * Пользователь, инициировавший заявку
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User requester;

    /**
     * Причина блокировки в свободной форме.
     */
    @Column(name = "reason", length = 150)
    private String reason;

    /**
     * Сотрудник, обработавший заявку; null до обработки.
     */
    @ManyToOne
    @JoinColumn(name = "processed_by")
    private User processedBy;

    /**
     * Текущий статус заявки.
     * Значение по умолчанию — {@link BlockRequestStatus#APPROVED}.
     */
    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Column(name = "block_request_status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BlockRequestStatus blockRequestStatus = BlockRequestStatus.APPROVED;

    /**
     * Время создания сущности на стороне приложения.
     * Устанавливается при инстанциировании.
     */
    @Builder.Default
    @Column(name = "create_at")
    private OffsetDateTime createAt = OffsetDateTime.now();
}










