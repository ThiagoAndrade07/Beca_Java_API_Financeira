package com.becajava.transactionservice.adapter.out.persistence.repository;

import com.becajava.transactionservice.adapter.out.persistence.entity.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, Long> {

    Optional<TransactionJpaEntity> findByIdAndOwnerUserId(
            Long id,
            Long ownerUserId
    );

    List<TransactionJpaEntity> findAllByOwnerUserId(Long ownerUserId);
}