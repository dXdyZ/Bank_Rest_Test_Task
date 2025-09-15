package com.example.bank_rest_test_task.repository;

import com.example.bank_rest_test_task.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {
    @EntityGraph(attributePaths = {"user"})
    Page<Card> findByUser_Id(Long userId, Pageable pageable);
    Optional<Card> findByEncryptNumberAndUser_Id(String encryptNumber, Long userId);
    Optional<Card> findByEncryptNumber(String encryptNumber);
    Optional<Card> findByIdAndUser_id(Long id, Long userId);
    Optional<Card> findBySearchHash(String searchHash);
    @EntityGraph(attributePaths = {"user"})
    Page<Card> findByUser_Username(String userUsername, Pageable pageable);
    Boolean existsBySearchHash(String searchHash);

}
