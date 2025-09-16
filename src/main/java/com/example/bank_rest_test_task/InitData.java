package com.example.bank_rest_test_task;

import com.example.bank_rest_test_task.entity.Card;
import com.example.bank_rest_test_task.entity.StatusCard;
import com.example.bank_rest_test_task.entity.User;
import com.example.bank_rest_test_task.entity.UserRole;
import com.example.bank_rest_test_task.repository.UserRepository;
import com.example.bank_rest_test_task.service.CardService;
import com.example.bank_rest_test_task.service.UserService;
import com.example.bank_rest_test_task.util.CryptoService;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class InitData {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CryptoService cryptoService;

    public InitData(UserRepository userRepository, PasswordEncoder passwordEncoder, CryptoService cryptoService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cryptoService = cryptoService;
    }

    @PostConstruct
    public void intiData() {
        if(userRepository.count() == 0) {
            String cardNumber = "5555555555555599";

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .role(UserRole.ROLE_ADMIN)
                .accountEnable(true)
                .accountLocked(false)
                .build();
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))
                .role(UserRole.ROLE_USER)
                .accountEnable(true)
                .accountLocked(false)
                .build();

        Card card = Card.builder()
                .encryptNumber(cryptoService.encrypt(cardNumber))
                .validityPeriod(LocalDate.now().plusYears(5))
                .statusCard(StatusCard.ACTIVE)
                .balance(BigDecimal.valueOf(600))
                .searchHash(cryptoService.calculationCardHash(cardNumber))
                .user(user)
                .last4("5599")
                .first8("55555555")
                .build();

        user.setCards(List.of(card));

        userRepository.save(user);
        userRepository.save(admin);
        }
    }
}









