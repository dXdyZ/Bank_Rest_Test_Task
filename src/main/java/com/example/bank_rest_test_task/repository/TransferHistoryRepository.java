package com.example.bank_rest_test_task.repository;

import com.example.bank_rest_test_task.entity.TransferHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Long> {
    @EntityGraph(attributePaths = {"user", "fromCard", "toCard"})
    Page<TransferHistory> findByUser_Username(String userUsername, Pageable pageable);
}
