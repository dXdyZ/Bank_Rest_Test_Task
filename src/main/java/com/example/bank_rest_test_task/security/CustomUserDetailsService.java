package com.example.bank_rest_test_task.security;

import com.example.bank_rest_test_task.entity.User;
import com.example.bank_rest_test_task.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Кастомная реализация {@link UserDetailsService}
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Получает пользователя по имени из базы данных и преобразовывает его в {@link CustomUserDetails}
     *
     * @param username имя пользователя который авторизовался
     * @return {@link UserDetails}
     * @throws UsernameNotFoundException если пользователя с таким именем не существует
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name())),
                user.getAccountEnable(),
                user.getAccountLocked()
        );
    }

    /**
     * Получает {@link CustomUserDetails} из {@link UserDetails}
     *
     * @param userDetails объект для преобразования
     * @return {@link CustomUserDetails}
     */
    public CustomUserDetails getCustomUserDetails(UserDetails userDetails) {
        if (!(userDetails instanceof CustomUserDetails customUserDetails)) {
            throw new IllegalArgumentException("UserDetails must be an instance of CustomUserDetails");
        }
        return customUserDetails;
    }

    /**
     * Получает пользователя по id из базы данных и преобразовывает его в {@link CustomUserDetails}
     *
     * @param id пользователя которого нужно найти
     * @return {@link UserDetails}
     * @throws UsernameNotFoundException если пользователя с таким именем не существует
     */
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name())),
                user.getAccountEnable(),
                user.getAccountLocked()
        );
    }
}
