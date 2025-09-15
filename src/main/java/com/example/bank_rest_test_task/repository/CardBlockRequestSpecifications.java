package com.example.bank_rest_test_task.repository;

import com.example.bank_rest_test_task.dto.CardBlockRequestFilter;
import com.example.bank_rest_test_task.entity.BlockRequestStatus;
import com.example.bank_rest_test_task.entity.CardBlockRequest;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public final class CardBlockRequestSpecifications {
    private CardBlockRequestSpecifications() {}

    // По статусу заявки
    public static Specification<CardBlockRequest> hasStatus(BlockRequestStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("blockRequestStatus"), status);
    }

    // Заявки обработаны/необработаны по флагу (processedAt null/not null)
    public static Specification<CardBlockRequest> processed(Boolean processed) {
        if (processed == null) return null;
        return (root, query, cb) -> processed
                ? cb.isNotNull(root.get("processedAt"))
                : cb.isNull(root.get("processedAt"));
    }

    // По инициатору (requester) — id
    public static Specification<CardBlockRequest> requesterId(Long requesterId) {
        return (root, query, cb) ->
                requesterId == null ? null : cb.equal(root.get("requester").get("id"), requesterId);
    }

    // По инициатору — username contains
    public static Specification<CardBlockRequest> requesterUsernameContains(String usernameFragment) {
        return (root, query, cb) -> {
            if (usernameFragment == null || usernameFragment.isBlank()) return null;
            var requester = root.join("requester", JoinType.INNER);
            String term = "%" + usernameFragment.trim().toLowerCase() + "%";
            return cb.like(cb.lower(requester.get("username")), term);
        };
    }

    // По обработчику (processedBy) — id
    public static Specification<CardBlockRequest> processedById(Long processedById) {
        return (root, query, cb) ->
                processedById == null ? null : cb.equal(root.get("processedBy").get("id"), processedById);
    }


    // По фрагменту причины (reason contains, case-insensitive)
    public static Specification<CardBlockRequest> reasonContains(String fragment) {
        return (root, query, cb) -> {
            if (fragment == null || fragment.isBlank()) return null;
            String term = "%" + fragment.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("reason")), term);
        };
    }

    // По дате создания (createAt) — диапазон
    public static Specification<CardBlockRequest> createdBetween(OffsetDateTime from, OffsetDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(root.get("createAt"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("createAt"), from);
            return cb.lessThanOrEqualTo(root.get("createAt"), to);
        };
    }

    // По дате обработки (processedAt) — диапазон
    public static Specification<CardBlockRequest> processedBetween(OffsetDateTime from, OffsetDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(root.get("processedAt"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("processedAt"), from);
            return cb.lessThanOrEqualTo(root.get("processedAt"), to);
        };
    }

    // Агрегатор: собрать спецификацию из фильтра
    public static Specification<CardBlockRequest> withFilter(CardBlockRequestFilter f) {
        if (f == null) return Specification.unrestricted();

        return Specification.allOf(
                hasStatus(f.getStatus()),
                processed(f.getProcessed()),
                requesterId(f.getRequesterId()),
                requesterUsernameContains(f.getRequesterUsername()),
                processedById(f.getProcessedById()),
                reasonContains(f.getReasonContains()),
                createdBetween(f.getCreatedFrom(), f.getCreatedTo()),
                processedBetween(f.getProcessedFrom(), f.getProcessedTo())
        );
    }
}