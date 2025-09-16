package com.example.bank_rest_test_task.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Кастомная реализация {@link UserDetails}
 */
public class CustomUserDetails implements UserDetails {

    @Getter
    private final Long userId;

    public final String username;
    public final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    private final Boolean accountEnable;

    /**
     * {@code false} - значит аккаунт не заблокирован
     * {@code true} - значит аккаунт заблокирован
     */
    private final Boolean accountLocked;

    public CustomUserDetails(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities, Boolean accountEnable, Boolean accountLocked) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.accountEnable = accountEnable;
        this.accountLocked = accountLocked;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * Возвращает признак того, что аккаунт НЕ заблокирован.
     * Основан на инверсии флага {@code accountLocked}.
     * <p>
     * Правила:
     * <ul>
     *   <li>{@code accountLocked = true} → метод вернёт {@code false} (аккаунт заблокирован)</li>
     *   <li>{@code accountLocked = false} → метод вернёт {@code true} (аккаунт не заблокирован)</li>
     * </ul>
     *
     * @return {@code true}, если аккаунт не заблокирован; {@code false}, если заблокирован
     */
    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return accountEnable;
    }
}
