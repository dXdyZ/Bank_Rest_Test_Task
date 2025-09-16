package com.example.bank_rest_test_task.repository;

import com.example.bank_rest_test_task.entity.CardBlockRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long>, JpaSpecificationExecutor<CardBlockRequest> {

    /**
     * Получает всех пользователей с использованием динамических запросов
     *
     * {@link EntityGraph} нужна для устранения 1+n запросов при выборке
     *
     * @param spec спецификация полученная на основе фильтров
     * @param pageable объект пагинации
     * @return результат выборки
     */
    @EntityGraph(attributePaths = {"card", "requester", "processedBy"})
    Page<CardBlockRequest> findAll(Specification<CardBlockRequest> spec, Pageable pageable);


    Page<CardBlockRequest> findAllByProcessedBy_Id(Long processedById, Pageable pageable);
}

