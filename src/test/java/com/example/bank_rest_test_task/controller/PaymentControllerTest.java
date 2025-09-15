package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.dto.PaymentDto;
import com.example.bank_rest_test_task.entity.Card;
import com.example.bank_rest_test_task.security.CustomUserDetails;
import com.example.bank_rest_test_task.service.PaymentService;
import com.example.bank_rest_test_task.util.factory.CardDtoFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private CardDtoFactory cardDtoFactory;

    @MockitoBean
    private Jwt jwt;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void transferMoney() throws Exception {
        PaymentDto paymentDto = new PaymentDto(1L, 2L, new BigDecimal("100.00"), "Test payment");
        when(jwt.getSubject()).thenReturn("1");
        when(paymentService.transferMoney(any(PaymentDto.class), anyLong())).thenReturn(Collections.singletonList(new Card()));

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentDto)).with(jwt().jwt(this.jwt).authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isOk());
    }
}