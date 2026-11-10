package com.becajava.transactionservice.domain.model;

import com.becajava.transactionservice.domain.exception.InvalidTransactionException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Long id;
    private Long ownerUserId;
    private Long destinationUserId;
    private BigDecimal amount;
    private LocalDateTime occurredAt;
    private String description;
    private TransactionCategory category;
    private TransactionType type;
    private TransactionStatus status;
    private String rejectionReason;
    private Long reversedTransactionId;


    public static Transaction createDeposit(
            Long ownerUserId,
            BigDecimal amount,
            String description,
            TransactionCategory category
    ) {
        if (ownerUserId == null) {
            throw new InvalidTransactionException(
                    "O usuário dono da transação é obrigatório"
            );
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException(
                    "O valor do depósito deve ser maior que zero"
            );
        }

        if (description == null || description.isBlank()) {
            throw new InvalidTransactionException(
                    "A descrição do depósito é obrigatória"
            );
        }

        if (category != TransactionCategory.INCOME) {
            throw new InvalidTransactionException(
                    "A categoria de um depósito deve ser INCOME"
            );
        }

        return new Transaction(
                ownerUserId,
                null,
                amount,
                LocalDateTime.now(),
                description.trim(),
                category,
                TransactionType.DEPOSIT,
                TransactionStatus.APPROVED,
                null,
                null
        );
    }


    public Transaction(Long ownerUserId, Long destinationUserId,
                       BigDecimal amount, LocalDateTime occurredAt,
                       String description, TransactionCategory category,
                       TransactionType type, TransactionStatus status,
                       String rejectionReason, Long reversedTransactionId) {
        this.ownerUserId = ownerUserId;
        this.destinationUserId = destinationUserId;
        this.amount = amount;
        this.occurredAt = occurredAt;
        this.description = description;
        this.category = category;
        this.type = type;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.reversedTransactionId = reversedTransactionId;
    }

    public Transaction(Long id, Long ownerUserId,
                       Long destinationUserId, BigDecimal amount,
                       LocalDateTime occurredAt, String description,
                       TransactionCategory category, TransactionType type,
                       TransactionStatus status, String rejectionReason,
                       Long reversedTransactionId) {
        this(ownerUserId, destinationUserId,
                amount, occurredAt,
                description, category,
                type, status,
                rejectionReason, reversedTransactionId
        );
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public Long getDestinationUserId() {
        return destinationUserId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public TransactionCategory getCategory() {
        return category;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public Long getReversedTransactionId() {
        return reversedTransactionId;
    }
}
