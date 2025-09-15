package com.example.bank_rest_test_task.repository;

import com.example.bank_rest_test_task.entity.User;
import com.example.bank_rest_test_task.entity.UserRole;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecification {
    /**
     * Регистронезависимый поиск по не полному совпадению имени
     *
     * @param usernameFragment имя пользователя может быть как полным так и какой-то из его частей при выборке будут
     *                         получены как и полные совпадения так и куда входит это подстрока
     * @return объект спецификации
     */
    public static Specification<User> hasUsername(String usernameFragment) {
        return (root, query, cb) -> {
            if (usernameFragment == null || usernameFragment.isBlank()) return null;

            String term = usernameFragment.trim().toLowerCase();

            return cb.like(cb.lower(root.get("username")), "%" + term + "%");
        };
    }

    /**
     * Поиск пользователя по роли
     *
     * @param roleName роль принимается в человеческом виде {@code user, admin}
     * @return объект спецификации
     */
    public static Specification<User> hasRole(String roleName) {
        return (root, query, cb) -> {
            if (roleName == null || roleName.isBlank()) return null;
            try {
                UserRole role = UserRole.valueOf(("ROLE_" + roleName.trim()).toUpperCase());
                return cb.equal(root.get("role"), role);
            } catch (IllegalArgumentException ex) {
                return cb.disjunction();
            }
        };
    }
}
