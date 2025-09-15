package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.controller.documentation.AuthControllerDocs;
import com.example.bank_rest_test_task.dto.AuthRequestDto;
import com.example.bank_rest_test_task.dto.JwtTokenDto;
import com.example.bank_rest_test_task.dto.RefreshTokenRequest;
import com.example.bank_rest_test_task.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/")
public class AuthController implements AuthControllerDocs {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(security = @SecurityRequirement(name = ""))
    public ResponseEntity<JwtTokenDto> login(@Valid @RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(authService.login(authRequestDto.username(), authRequestDto.password()));
    }

    @PostMapping("/refresh")
    @Operation(security = @SecurityRequirement(name = ""))
    public ResponseEntity<JwtTokenDto> updateAccessToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshTokenRequest.getRefreshToken()));
    }
}
