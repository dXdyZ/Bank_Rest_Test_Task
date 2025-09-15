package com.example.bank_rest_test_task.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;


/**
 * JPA-entity, представляет пользователя
 *
 * - {@link #role} - роль пользователя; автоматически при создании пользователя назначается {@link UserRole#ROLE_USER}
 * - {@link #cards} - карты пользователя; создаются администратором
 * - {@link #password} - Односторонне захешированный пароль; Хеширует {@link BCryptPasswordEncoder#encode(CharSequence)}
 *
 */
@Getter
@Setter
@Entity
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Уникальный идентификатор (PK), генерируется БД (IDENTITY)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя
     */
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /**
     * Хешированный пароль пользователя
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Роль пользователя
     * По умолчанию {@link UserRole#ROLE_USER}
     */
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * Банковские карты пользователя
     */
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @BatchSize(size = 50)
    @Fetch(FetchMode.SUBSELECT)
    private List<Card> cards = new ArrayList<>();

    /**
     * Активная ли учетная запись
     * По умолчанию {@code true}
     */
    @Builder.Default
    @Column(name = "account_enable")
    private Boolean accountEnable = true;

    /**
     * Заблокирована ли учетная запись пользователя
     * По умолчанию {@code false}
     */
    @Builder.Default
    @Column(name = "account_locked")
    private Boolean accountLocked = false;
}
