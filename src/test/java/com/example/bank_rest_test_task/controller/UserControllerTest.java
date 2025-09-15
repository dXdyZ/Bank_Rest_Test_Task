package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.dto.UserDto;
import com.example.bank_rest_test_task.dto.UserRegisterDto;
import com.example.bank_rest_test_task.dto.UserRoleUpdateDto;
import com.example.bank_rest_test_task.dto.UsernameUpdateDto;
import com.example.bank_rest_test_task.entity.User;
import com.example.bank_rest_test_task.service.UserService;
import com.example.bank_rest_test_task.util.factory.UserDtoFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDtoFactory userDtoFactory;

    @MockitoBean
    private Jwt jwt;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto("user", "password");
        doNothing().when(userService).registrationUser(any(UserRegisterDto.class));

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDto)).with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getUserByUsername() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        when(userService.findUserByUsername(anyString())).thenReturn(new User());
        when(userDtoFactory.createUserDtoAndCardDtoForAdminWithCards(any(User.class))).thenReturn(new UserDto());

        mockMvc.perform(get("/users/by-username/user").with(jwt().jwt(this.jwt)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void deleteUserByUsername() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        doNothing().when(userService).deleteUserByUsername(anyString());
        mockMvc.perform(delete("/users/by-username/user").with(jwt().jwt(this.jwt)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void deleteUserById() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        doNothing().when(userService).deleteUserById(anyLong());
        mockMvc.perform(delete("/users/1").with(jwt().jwt(this.jwt)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void updateUsername() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        UsernameUpdateDto usernameUpdateDto = new UsernameUpdateDto("newUsername");
        when(userService.updateUsername(anyString(), anyLong())).thenReturn(new User());
        when(userDtoFactory.createUserDtoAndCardDtoForAdminWithCards(any(User.class))).thenReturn(new UserDto());

        mockMvc.perform(patch("/users/1/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usernameUpdateDto)).with(jwt().jwt(this.jwt)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void updateRol() throws Exception {
        when(jwt.getSubject()).thenReturn("1");
        UserRoleUpdateDto userRoleUpdateDto = new UserRoleUpdateDto("ADMIN");
        when(userService.updateRole(any(String.class), anyLong())).thenReturn(new User());
        when(userDtoFactory.createUserDtoAndCardDtoForAdminWithCards(any(User.class))).thenReturn(new UserDto());

        mockMvc.perform(patch("/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRoleUpdateDto)).with(jwt().jwt(this.jwt)))
                .andExpect(status().isOk());
    }
}