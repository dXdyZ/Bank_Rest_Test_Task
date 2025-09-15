package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.dto.AuthRequestDto;
import com.example.bank_rest_test_task.dto.JwtTokenDto;
import com.example.bank_rest_test_task.dto.RefreshTokenRequest;
import com.example.bank_rest_test_task.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login() throws Exception {
        AuthRequestDto authRequestDto = new AuthRequestDto("user", "password");
        JwtTokenDto jwtTokenDto = new JwtTokenDto("accessToken", "refreshToken");
        when(authService.login(anyString(), anyString())).thenReturn(jwtTokenDto);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateAccessToken() throws Exception {
        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder().refreshToken("refreshToken").build();
        JwtTokenDto jwtTokenDto = new JwtTokenDto("newAccessToken", "newRefreshToken");
        when(authService.refreshAccessToken(anyString())).thenReturn(jwtTokenDto);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk());
    }
}