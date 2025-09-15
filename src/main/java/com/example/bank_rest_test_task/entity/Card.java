package com.example.bank_rest_test_task.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * JPA-entity, представляет банковскую карту
 *
 * - {@link #encryptNumber} - зашифрованный номер карты; шифруется во время создания
 * - {@link #searchHash} - hash код вычисляемый во время создания карты на основе полного номера карты
 * - {@link #user} - пользователь, которому принадлежит карта
 *
 * Особенности:
 * - Связь @ManyToOne без каскадов: связанный {@link User} должны существовать в бд.
 */
@Getter
@Setter
@Entity
@Builder
@Table(name = "cards")
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    /**
     * Уникальный идентификатор (PK), генерируется БД (IDENTITY)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Зашифрованный номер карты
     */
    @Column(name = "encrypted_number", unique = true, nullable = false)
    private String encryptNumber;

    /**
     * Годна до указанной даты
     */
    @Column(name = "validity_period", nullable = false)
    private LocalDate validityPeriod;

    /**
     * Статус карты
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status_card", nullable = false)
    private StatusCard statusCard;

    /**
     * Баланс карты
     * При создании 0
     */
    @Builder.Default
    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Hash код для поиска карты
     */
    @Column(name = "search_hash", nullable = false)
    private String searchHash;

    /**
     * Пользователь, которому принадлежит карта
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Первые 8 цифр для поиска по началу номера
     */
    @Column(name = "first8", length = 8, nullable = false)
    private String first8;

    /**
     * Последние 4 цифры для поиска по концовке
     */
    @Column(name = "last4", length = 4, nullable = false)
    private String last4;
}
