package com.becajava.transactionservice.adapter.out.persistence.repository;

import com.becajava.transactionservice.adapter.out.persistence.entity.TransactionJpaEntity;
import com.becajava.transactionservice.domain.model.TransactionStatus;
import com.becajava.transactionservice.domain.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, Long> {

    Optional<TransactionJpaEntity> findByIdAndOwnerUserId(
            Long id,
            Long ownerUserId
    );

    List<TransactionJpaEntity> findAllByOwnerUserId(Long ownerUserId);

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM TransactionJpaEntity t
        WHERE t.ownerUserId = :ownerUserId
          AND t.status = :status
          AND t.type IN :types
          AND t.occurredAt >= :start
          AND t.occurredAt < :end
        """)
    BigDecimal sumOutgoingAmountByOwnerUserIdBetween(
            @Param("ownerUserId") Long ownerUserId,
            @Param("status") TransactionStatus status,
            @Param("types") List<TransactionType> types,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}