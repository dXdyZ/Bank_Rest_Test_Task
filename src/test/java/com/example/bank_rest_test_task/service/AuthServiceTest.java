package com.example.bank_rest_test_task.service;

import com.example.bank_rest_test_task.dto.JwtTokenDto;
import com.example.bank_rest_test_task.exception.AuthenticationFailedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    TokenService tokenService;

    @InjectMocks
    AuthService authService;

    @Test
    public void whenValidCredentials_thenReturnTokens() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        JwtTokenDto expectedTokens = new JwtTokenDto("access123", "refresh456");
        when(tokenService.getTokens("user1")).thenReturn(expectedTokens);

        JwtTokenDto result = authService.login("user1", "password123");

        assertNotNull(result);
        assertEquals("access123", result.getAccessToken());
        assertEquals("refresh456", result.getRefreshToken());
    }

    @Test
    public void whenInvalidCredentials_thenThrowBadCredentialsException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.login("user1", "wrong_password")
        );

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    public void whenAccountLocked_thenThrowLockedException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new LockedException("Account locked"));

        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.login("user1", "password123")
        );

        assertEquals("Account is locked", exception.getMessage());
    }

    @Test
    public void whenAccountDisabled_thenThrowDisabledException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("Account disabled"));

        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.login("user1", "password123")
        );

        assertEquals("Account is disable", exception.getMessage());
    }
}