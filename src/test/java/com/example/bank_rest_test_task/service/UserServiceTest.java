package com.example.bank_rest_test_task.service;

import com.example.bank_rest_test_task.dto.UserRegisterDto;
import com.example.bank_rest_test_task.entity.User;
import com.example.bank_rest_test_task.entity.UserRole;
import com.example.bank_rest_test_task.exception.DuplicateUserException;
import com.example.bank_rest_test_task.exception.UserNotFoundException;
import com.example.bank_rest_test_task.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void existUserByUsername_WhenUserExists_ThenReturnTrue() {
        String username = "test";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        boolean result = userService.existUserByUsername(username);

        assertTrue(result);
    }

    @Test
    void existUserByUsername_WhenDoesntExistsUser_ThenReturnFalse() {
        String username = "test";

        when(userRepository.existsByUsername(username)).thenReturn(false);

        boolean result = userService.existUserByUsername(username);

        assertFalse(result);
    }

    @Test
    void findUserByUsername_WhenUserExists_ThenReturnUser() {
        String username = "user";
        User user = User.builder()
                .id(1L)
                .username("user")
                .password("123445")
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.findUserByUsername(username);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void findUserByUsername_WhenDoesntExistsUser_ThenThrowUserNotFoundException() {
        String username = "user";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.findUserByUsername(username));

        assertInstanceOf(UserNotFoundException.class, exception);
        assertEquals("User by name: user not found", exception.getMessage());
    }

    @Test
    void registrationUser_WhenDoesntExistsUser() {
        String username = "user";
        String password = "password";
        String encodePassword = "encodePassword";
        UserRegisterDto regDto = new UserRegisterDto(username, password);

        when(passwordEncoder.encode(password)).thenReturn(encodePassword);
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(
                User.builder()
                        .username(username)
                        .password(encodePassword)
                        .role(UserRole.ROLE_USER)
                        .build()
        );

        userService.registrationUser(regDto, 1L);

        verify(userRepository).existsByUsername(username);
        verify(userRepository).save(argThat(actUser ->
                actUser.getUsername().equals(username) &&
                actUser.getPassword().equals(encodePassword) &&
                actUser.getRole() == UserRole.ROLE_USER &&
                actUser.getAccountEnable() == true &&
                actUser.getAccountLocked() == false));
    }

    @Test
    void registrationUser_WhenExistsDuplicateUser_ThenThrowDuplicateUserException() {
        String username = "user";
        String password = "password";
        UserRegisterDto regDto = new UserRegisterDto(username, password);

        when(userRepository.existsByUsername(username)).thenReturn(true);

        DuplicateUserException exception = assertThrows(DuplicateUserException.class,
                () -> userService.registrationUser(regDto, 1L));

        assertInstanceOf(DuplicateUserException.class, exception);
        assertEquals("User by name: user already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUsername_WhenUserExistsAndDoesntDuplicateUser() {
        String username = "newUsername";
        Long id = 1L;
        User user = User.builder()
                .id(1L)
                .username("username")
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.save(argThat(saveUser -> saveUser.getUsername().equals(username)))).thenReturn(User.builder()
                        .id(id)
                        .username(username)
                .build());

        User result = userService.updateUsername(username, id, 1L);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository).save(argThat(actUser -> actUser.getUsername().equals(username)));
    }

    @Test
    void updateUsername_WhenDoesntExists_ThenThrowUserNotFoundException() {
        String username = "newUsername";
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.updateUsername(username, id, 1L));

        assertInstanceOf(UserNotFoundException.class, exception);
        assertEquals("User by id: 1 not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUsername_WhenDuplicateUserExists_ThenThrowDuplicateUserException() {
        String username = "newUsername";
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.of(new User()));
        when(userRepository.existsByUsername(username)).thenReturn(true);

        DuplicateUserException exception = assertThrows(DuplicateUserException.class,
                () -> userService.updateUsername(username, id, 1L));

        assertInstanceOf(DuplicateUserException.class, exception);
        assertEquals("User by name: newUsername already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateRole_WhenUserExistsAndRoleNameIsValid() {
        String roleName = "ADMIN";
        Long id = 1L;
        User user = User.builder()
                .id(id)
                .role(UserRole.ROLE_USER)
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(argThat(actUser -> actUser.getRole() == UserRole.ROLE_ADMIN))).thenReturn(User.builder()
                        .id(id)
                        .role(UserRole.ROLE_ADMIN)
                .build());

        User result = userService.updateRole(roleName, id, 1L);

        assertNotNull(result);
        assertEquals(UserRole.ROLE_ADMIN, result.getRole());
    }


    @Test
    void updateRole_WhenDoesntUserExistsAndRoleNameIsValid_ThenThrowUserNotFoundException() {
        String roleName = "ADMIN";
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.updateRole(roleName, id, 1L));

        assertInstanceOf(UserNotFoundException.class, exception);
        assertEquals("User by id: 1 not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}












