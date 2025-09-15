package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.dto.*;
import com.example.bank_rest_test_task.entity.BlockRequestStatus;
import com.example.bank_rest_test_task.entity.CardBlockRequest;
import com.example.bank_rest_test_task.entity.User;
import com.example.bank_rest_test_task.security.CustomUserDetails;
import com.example.bank_rest_test_task.service.CardBlockRequestService;
import com.example.bank_rest_test_task.util.factory.CardBlockRequestDtoFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@WebMvcTest(CardBlockController.class)
public class CardBlockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardBlockRequestService blockService;

    @MockitoBean
    private CardBlockRequestDtoFactory cardBlockRequestDtoFactory;

    @MockitoBean
    private Jwt jwt;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBlockRequest() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        CreateCardBlockRequestDto createCardBlockRequestDto = new CreateCardBlockRequestDto(1L, "1234567812345678");
        doNothing().when(blockService).createBlockRequest(any(CreateCardBlockRequestDto.class), anyLong());

        mockMvc.perform(post("/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCardBlockRequestDto)).with(jwt().jwt(this.jwt).authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void processBlockRequest() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        ProcessBlockRequestDto processBlockRequestDto = new ProcessBlockRequestDto();
        processBlockRequestDto.setRequestId(1L);
        processBlockRequestDto.setStatus(BlockRequestStatus.APPROVED);
        processBlockRequestDto.setAdminId(1L);
        doNothing().when(blockService).processBlockRequest(anyLong(), any(BlockRequestStatus.class), anyLong());

        mockMvc.perform(put("/blocks/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(processBlockRequestDto)).with(jwt().jwt(this.jwt).authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void findFilterCardBlockRequest() throws Exception {
        when(jwt.getSubject()).thenReturn("1");

        CardBlockRequest request = CardBlockRequest.builder()
                .id(1L)
                .requester(User.builder()
                        .id(1L)
                        .username("user")
                        .build())
                .reason("Block")
                .build();

        Page<CardBlockRequest> page = new PageImpl<>(Collections.singletonList(request));

        CardBlockRequestFilter filter = CardBlockRequestFilter.builder()
                .requesterUsername("user")
                .build();

        when(blockService.searCardBlockRequest(any(), any(PageRequest.class))).thenReturn(page);

        when(cardBlockRequestDtoFactory.creatCardBlockRequestDto(any(CardBlockRequest.class))).thenReturn(new CardBlockRequestDto());

        when(cardBlockRequestDtoFactory.creatCardBlockRequestDto(any(CardBlockRequest.class)))
                .thenReturn(CardBlockRequestDto.builder()
                        .id(1L).requester(UserDto.builder().id(1L).username("user").build()).reason("Block").build());


        mockMvc.perform(post("/blocks/search").with(jwt().jwt(this.jwt).authorities(new SimpleGrantedAuthority("ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].requester.username").value("user"));
    }

    @Test
    @WithMockUser
    void findCardBlockRequestByProcessed() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        Page<CardBlockRequest> page = new PageImpl<>(Collections.singletonList(new CardBlockRequest()));
        when(blockService.findCardBlockRequestByProcessed(anyLong(), any(PageRequest.class))).thenReturn(page);
        when(cardBlockRequestDtoFactory.creatCardBlockRequestDto(any(CardBlockRequest.class))).thenReturn(new CardBlockRequestDto());

        mockMvc.perform(get("/blocks/processed-by/1").with(jwt().jwt(this.jwt).authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void findCardBlockRequestById() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        when(blockService.findCardBlockRequestById(anyLong())).thenReturn(new CardBlockRequest());
        when(cardBlockRequestDtoFactory.creatCardBlockRequestDto(any(CardBlockRequest.class))).thenReturn(new CardBlockRequestDto());

        mockMvc.perform(get("/blocks/1").with(jwt().jwt(this.jwt).authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());
    }
}