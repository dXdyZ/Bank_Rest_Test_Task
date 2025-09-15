package com.example.bank_rest_test_task.repository;

import com.example.bank_rest_test_task.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    default Page<User> searchUser(String username, String roleName, Pageable pageable) {
        Specification<User> specification = Specification.unrestricted();

        specification = specification
                .and(UserSpecification.hasUsername(username))
                .and(UserSpecification.hasRole(roleName));

        return findAll(specification, pageable);
    }
}







