package com.example.bank_rest_test_task.repository;

import com.example.bank_rest_test_task.dto.CardSearchFilter;
import com.example.bank_rest_test_task.entity.Card;
import com.example.bank_rest_test_task.util.CryptoService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public final class CardSpecification {
    private CardSpecification() {}

    public static Specification<Card> withFilter(CardSearchFilter f, CryptoService crypto) {
        if (f == null) return Specification.unrestricted();

        return (root, query, cb) -> {
            var p = new ArrayList<Predicate>();

            if (f.getUserId() != null) {
                p.add(cb.equal(root.get("user").get("id"), f.getUserId()));
            }

            if (f.getStatus() != null) {
                p.add(cb.equal(root.get("statusCard"), f.getStatus()));
            }

            if (f.getValidFrom() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("validityPeriod"), f.getValidFrom()));
            }
            if (f.getValidTo() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("validityPeriod"), f.getValidTo()));
            }

            if (f.getBalanceMin() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("balance"), f.getBalanceMin()));
            }
            if (f.getBalanceMax() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("balance"), f.getBalanceMax()));
            }

            // Точный поиск по searchHash
            if (notBlank(f.getFullNumber())) {
                String digits = onlyDigits(f.getFullNumber());
                if (!digits.isEmpty()) {
                    String hash = crypto.calculationCardHash(digits);
                    p.add(cb.equal(root.get("searchHash"), hash));
                }
            }

            // По началу номера
            if (notBlank(f.getFirst8Number())) {
                String prefix = onlyDigits(f.getFirst8Number());
                if (!prefix.isEmpty()) {
                    p.add(cb.like(root.get("first8"), prefix + "%"));
                }
            }

            // По концовке номера
            if (notBlank(f.getLast4Number())) {
                String suffix = onlyDigits(f.getLast4Number());
                if (!suffix.isEmpty()) {
                    if (suffix.length() == 4) {
                        p.add(cb.equal(root.get("last4"), suffix));
                    } else {
                        p.add(cb.like(root.get("last4"), "%" + suffix));
                    }
                }
            }

            return p.isEmpty() ? null : cb.and(p.toArray(new Predicate[0]));
        };
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private static String onlyDigits(String s) {
        return s.trim().replaceAll("\\D", "");
    }
}
