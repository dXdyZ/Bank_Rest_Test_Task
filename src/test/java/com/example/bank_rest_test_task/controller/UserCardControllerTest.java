package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.dto.CardDto;
import com.example.bank_rest_test_task.entity.Card;
import com.example.bank_rest_test_task.entity.StatusCard;
import com.example.bank_rest_test_task.security.CustomUserDetails;
import com.example.bank_rest_test_task.service.CardService;
import com.example.bank_rest_test_task.util.factory.CardDtoFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(UserCardController.class)
public class UserCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private CardDtoFactory cardDtoFactory;

    @MockitoBean
    private Jwt jwt;

    @Test
    @WithMockUser
    void getCardById() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        when(cardService.findCardByUserIdAndCardId(anyLong(), anyLong())).thenReturn(new Card());
        when(cardDtoFactory.createCardDtoForUser(any(Card.class))).thenReturn(new CardDto(1L, "1234567812345678", LocalDate.now().plusYears(5), StatusCard.ACTIVE, BigDecimal.ZERO));

        mockMvc.perform(get("/cards/1").with(jwt().jwt(this.jwt)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getUserCards() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        Page<Card> page = new PageImpl<>(Collections.singletonList(new Card()));
        when(cardService.getUserCardsPaginated(anyLong(), any(PageRequest.class))).thenReturn(page);
        when(cardDtoFactory.createCardDtoForUser(any(Card.class))).thenReturn(new CardDto(1L, "1234567812345678", LocalDate.now().plusYears(5), StatusCard.ACTIVE, BigDecimal.ZERO));

        mockMvc.perform(get("/cards").with(jwt().jwt(this.jwt)))
                .andExpect(status().isOk());
    }
}