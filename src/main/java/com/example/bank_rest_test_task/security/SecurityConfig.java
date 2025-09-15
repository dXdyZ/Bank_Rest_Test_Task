package com.example.bank_rest_test_task.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import com.example.bank_rest_test_task.security.jwt.JwtKeyConfiguration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.List;


/**
 * Конфигурация безопасности приложения.
 *
 * Основные настройки:
 * - Stateless: сессии не используются (JWT в заголовке).
 * - HTTP Basic - отключен.
 * - Приложение работает как ресурс-сервер (OAuth2) c проверкой JWT.
 * - Публичные эндпоинты: /auth/**, swagger/openapi ресурсы, /error.
 * - Ограничения по ролям (ROLE_USER / ROLE_ADMIN) на остальные маршруты.
 * - Роли и права извлекаются из claim "authorities" токена без префикса "ROLE_".
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtKeyConfiguration jwtKeyConfiguration;

    public SecurityConfig(JwtKeyConfiguration jwtKeyConfiguration) {
        this.jwtKeyConfiguration = jwtKeyConfiguration;
    }


    /**
     * Основная цепочка фильтров безопасности.
     * Настраивает:
     * - stateless-сессии;
     * - проверку JWT;
     * - доступ по ролям;
     * - разрешённые публичные маршруты.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/error").permitAll()
                        .requestMatchers("/admin/cards/**").hasRole("ADMIN")
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/blocks/process", "/blocks/filter", "/blocks/processed-by/**", "/blocks/{id}").hasRole("ADMIN")
                        .requestMatchers("/cards/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/payments/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/blocks").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                
                .build();
    }


    /**
     * Конвертер JWT -> Authentication.
     * Берёт роли из claim "authorities" без добавления префикса "ROLE_".
     */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    /**
     * Декодер JWT на основе публичного RSA ключа.
     * Бросает IllegalStateException, если ключ недоступен.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            return NimbusJwtDecoder.withPublicKey((RSAPublicKey) jwtKeyConfiguration.keyPair().getPublic()).build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load public key for JWT decoding", e);
        }
    }

    /**
     * Менеджер аутентификации (делегирует Spring Security).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
