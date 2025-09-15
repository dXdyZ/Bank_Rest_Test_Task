package com.example.bank_rest_test_task.security.jwt;

import com.example.bank_rest_test_task.exception.InvalidJwtTokenException;
import com.example.bank_rest_test_task.security.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtKeyConfiguration jwtKeyConfiguration;
    private final KeyPair keyPair;

    public JwtUtils(CustomUserDetailsService customUserDetailsService, JwtKeyConfiguration jwtKeyConfiguration, KeyPair keyPair) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtKeyConfiguration = jwtKeyConfiguration;
        this.keyPair = keyPair;
    }

    public String generationAccessToken(UserDetails userDetails) {
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(customUserDetailsService.getCustomUserDetails(userDetails).getUserId().toString())
                .claim("authorities", authorities)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtKeyConfiguration.getAccessExpiration()))
                .signWith(keyPair.getPrivate())
                .compact();
    }

    public String generationRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(customUserDetailsService.getCustomUserDetails(userDetails).getUserId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtKeyConfiguration.getRefreshExpiration()))
                .signWith(keyPair.getPrivate())
                .compact();
    }

    public boolean validationToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtTokenException("Invalid or expired JWT token");
        }
    }
}
