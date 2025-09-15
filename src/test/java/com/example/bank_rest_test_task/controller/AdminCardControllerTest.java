package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.dto.CardCreateDto;
import com.example.bank_rest_test_task.dto.CardDto;
import com.example.bank_rest_test_task.dto.UpdateStatusCardDto;
import com.example.bank_rest_test_task.entity.Card;
import com.example.bank_rest_test_task.entity.StatusCard;
import com.example.bank_rest_test_task.service.CardService;
import com.example.bank_rest_test_task.util.factory.CardDtoFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@WebMvcTest(AdminCardController.class)
public class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private CardDtoFactory cardDtoFactory;

    @MockitoBean
    private Jwt jwt;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCard() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        CardCreateDto cardCreateDto = new CardCreateDto(1L, "4111111111111111", LocalDate.now().plusYears(5));
        doNothing().when(cardService).createCard(any(CardCreateDto.class));

        mockMvc.perform(post("/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardCreateDto)).with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isCreated());
    }

    @Test
    void getCardById() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        Card card = new Card();
        card.setId(1L);
        CardDto cardDto = new CardDto(1L, "1234567812345678", LocalDate.now().plusYears(5), StatusCard.ACTIVE, BigDecimal.ZERO);
        when(cardService.findCardById(anyLong())).thenReturn(card);
        when(cardDtoFactory.createCardDtoForAdmin(any(Card.class))).thenReturn(cardDto);

        mockMvc.perform(get("/admin/cards/1").with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void getUserCards() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        Page<Card> page = new PageImpl<>(Collections.singletonList(new Card()));
        when(cardService.getUserCardsPaginated(anyLong(), any(PageRequest.class))).thenReturn(page);
        when(cardDtoFactory.createCardDtoForAdmin(any(Card.class))).thenReturn(new CardDto(1L, "1234567812345678", LocalDate.now().plusYears(5), StatusCard.ACTIVE, BigDecimal.ZERO));

        mockMvc.perform(get("/admin/cards/user/1").with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void getCardByCardNumber() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        Card card = new Card();
        card.setId(1L);
        CardDto cardDto = new CardDto(1L, "1234567812345678", LocalDate.now().plusYears(5), StatusCard.ACTIVE, BigDecimal.ZERO);
        when(cardService.findCardByNumber(any(String.class))).thenReturn(card);
        when(cardDtoFactory.createCardDtoForAdmin(any(Card.class))).thenReturn(cardDto);

        mockMvc.perform(get("/admin/cards/by-number").param("number", "49927398716").with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatusCard() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        UpdateStatusCardDto updateStatusCardDto = new UpdateStatusCardDto(StatusCard.BLOCKED);
        Card card = new Card();
        card.setId(1L);
        CardDto cardDto = new CardDto(1L, "1234567812345678", LocalDate.now().plusYears(5), StatusCard.BLOCKED, BigDecimal.ZERO);
        when(cardService.updateCardStatus(anyLong(), any(StatusCard.class))).thenReturn(card);
        when(cardDtoFactory.createCardDtoForAdmin(any(Card.class))).thenReturn(cardDto);

        mockMvc.perform(patch("/admin/cards/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusCardDto)).with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCardById() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        doNothing().when(cardService).deleteCardById(anyLong());
        mockMvc.perform(delete("/admin/cards/1").with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCardByCardNumber() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        doNothing().when(cardService).deleteCardByCardNumber(any(String.class));
        mockMvc.perform(delete("/admin/cards/by-number").param("number", "4111111111111111").with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isNoContent());
    }
}