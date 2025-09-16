package com.example.bank_rest_test_task.service;

import com.example.bank_rest_test_task.dto.JwtTokenDto;
import com.example.bank_rest_test_task.exception.AuthenticationFailedException;
import com.example.bank_rest_test_task.security.jwt.JwtUtils;
import com.example.bank_rest_test_task.util.LogMarker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.bank_rest_test_task.security.CustomUserDetailsService;

import org.springframework.stereotype.Service;


/**
 * Сервис работы с JWT: выдача пары (access/refresh) и обновление access по refresh.
 *
 * Проверяет валидность refresh и извлекает данные пользователя для формирования новых токенов.
 */
@Slf4j
@Service
public class TokenService {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * @param jwtUtils проверяет и проверяет токен
     * @param customUserDetailsService получает {@link UserDetails}
     */
    public TokenService(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Получает {@link UserDetails} и access и refresh токены с помощью {@link JwtUtils#generationAccessToken(UserDetails)} и {@link JwtUtils#generationRefreshToken(UserDetails)} (UserDetails)}
     *
     * @param username имя пользователя из {@link UserDetails}
     * @return {@link JwtTokenDto} с access и refresh токенами
     */
    public JwtTokenDto getTokens(String username) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        return JwtTokenDto.builder()
                .accessToken(jwtUtils.generationAccessToken(userDetails))
                .refreshToken(jwtUtils.generationRefreshToken(userDetails))
                .build();
    }

    /**
     * Проверяет refresh токен с помощью метода {@link JwtUtils#validationToken(String)} и возвращает новый access токен с помощью метода {@link JwtUtils#generationAccessToken(UserDetails)}
     *
     * @param refreshToken токен передаваемый пользователем
     * @return {@link JwtTokenDto} новый access токен
     */
    public JwtTokenDto refreshAccessToken(String refreshToken) {
        if (!jwtUtils.validationToken(refreshToken)) {
            log.info(LogMarker.LOGIN.getMarker(), "action=REFRESH_TOKEN | result=FAILURE | reason=INVALID_REFRESH_TOKEN | username=-");
            throw new AuthenticationFailedException("Refresh token is invalid or expired. Please log in again");
        }
        UserDetails userDetails = customUserDetailsService.loadUserById(Long.valueOf(jwtUtils.extractUserId(refreshToken)));

        if (!userDetails.isAccountNonLocked()) {
            log.warn(LogMarker.LOGIN.getMarker(), "action=REFRESH_TOKEN | result=FAILURE | reason=ACCOUNT_LOCKED | username={}", userDetails.getUsername());
            throw new AuthenticationFailedException("Account is locked");
        }

        if (!userDetails.isEnabled()) {
            log.warn(LogMarker.LOGIN.getMarker(), "action=REFRESH_TOKEN | result=FAILURE | reason=ACCOUNT_DISABLE | username={}", userDetails.getUsername());
            throw new AuthenticationFailedException("Account is disabled");
        }

        log.info(LogMarker.LOGIN.getMarker(), "action=REFRESH_TOKEN | result=SUCCESSFULLY | reason=- | username={}", userDetails.getUsername());

        return JwtTokenDto.builder()
                .accessToken(jwtUtils.generationAccessToken(userDetails))
                .build();
    }
}
