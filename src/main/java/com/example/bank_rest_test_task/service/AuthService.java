package com.example.bank_rest_test_task.service;

import com.example.bank_rest_test_task.dto.JwtTokenDto;
import com.example.bank_rest_test_task.exception.AuthenticationFailedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Сервис аутентификации: проверяет учетные данные и выдает JWT токены.
 *
 * Процесс:
 * - аутентифицирует пользователя через AuthenticationManager;
 * - формирует access / refresh токены через TokenService.
 */
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    /**
     * @param authenticationManager проверяет учетный данные и авторизуется пользователя
     * @param tokenService сервис для генерации JWT токена
     */
    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    /**
     * Авторизует и выдает токен пользователю
     *
     * @param username имя пользователя, передаваемое пользователем
     * @param password пароль пользователя, передаваемый пользователем
     * @return JWT токен
     * @throws AuthenticationFailedException если не верные учетный данные или аккаунт не активен или заблокирован
     */
    public JwtTokenDto login(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            password
                    )
            );
            return tokenService.getTokens(authentication.getName());
        } catch (DisabledException e) {
            throw new AuthenticationFailedException("Account is disable");
        } catch (LockedException e) {
            throw new AuthenticationFailedException("Account is locked");
        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Invalid username or password");
        }
    }

    /**
     * Обновляет JWT Access токен на основе Refresh токена
     *
     * @param refreshToken токен для обновления access токена
     * @return новый access токен
     * @throws AuthenticationFailedException если истек или не валидный refresh токен
     */
    public JwtTokenDto refreshAccessToken(String refreshToken) throws AuthenticationFailedException{
        return tokenService.refreshAccessToken(refreshToken);
    }
}
