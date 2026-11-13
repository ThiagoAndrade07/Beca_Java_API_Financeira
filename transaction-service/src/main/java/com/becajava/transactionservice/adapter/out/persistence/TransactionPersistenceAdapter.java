package com.becajava.transactionservice.adapter.out.persistence;

import com.becajava.transactionservice.adapter.out.persistence.entity.TransactionJpaEntity;
import com.becajava.transactionservice.adapter.out.persistence.mapper.TransactionPersistenceMapper;
import com.becajava.transactionservice.adapter.out.persistence.repository.TransactionJpaRepository;
import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionStatus;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.out.TransactionRepositoryPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {

    private final TransactionJpaRepository transactionJpaRepository;

    public TransactionPersistenceAdapter(TransactionJpaRepository transactionJpaRepository) {
        this.transactionJpaRepository = transactionJpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity entity = TransactionPersistenceMapper.toJpaEntity(transaction);

        TransactionJpaEntity savedEntity = transactionJpaRepository.save(entity);

        return TransactionPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Transaction> findByIdAndOwnerUserId(Long id, Long ownerUserId) {
        return transactionJpaRepository.findByIdAndOwnerUserId(id, ownerUserId).map(TransactionPersistenceMapper::toDomain);
    }

    @Override
    public List<Transaction> findAllByOwnerUserId(Long ownerUserId) {
        List<TransactionJpaEntity> entities = transactionJpaRepository.findAllByOwnerUserId(ownerUserId);

        List<Transaction> transactions = new ArrayList<>();

        for (TransactionJpaEntity entity : entities) {
            Transaction transaction = TransactionPersistenceMapper.toDomain(entity);

            transactions.add(transaction);
        }

        return transactions;
    }

    @Override
    public BigDecimal sumApprovedOutgoingAmountByOwnerUserIdBetween(Long ownerUserId, LocalDateTime start, LocalDateTime end) {
        BigDecimal total =
                transactionJpaRepository
                        .sumOutgoingAmountByOwnerUserIdBetween(
                                ownerUserId,
                                TransactionStatus.APPROVED,
                                List.of(
                                        TransactionType.WITHDRAWAL,
                                        TransactionType.TRANSFER,
                                        TransactionType.PURCHASE
                                ),
                                start,
                                end
                        );

        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<Transaction> findAllByOwnerUserIdAndFilters(Long ownerUserId, TransactionType type, LocalDateTime start, LocalDateTime end) {
        List<TransactionJpaEntity> entities;

        if (type == null) {
            entities = transactionJpaRepository.findAllByOwnerUserIdAndPeriod(ownerUserId, start, end);
        } else {
            entities = transactionJpaRepository.findAllByOwnerUserIdAndTypeAndPeriod(ownerUserId, type, start, end);
        }

        List<Transaction> transactions = new ArrayList<>();

        for (TransactionJpaEntity entity : entities) {
            transactions.add(TransactionPersistenceMapper.toDomain(entity));
        }

        return transactions;
    }
}
