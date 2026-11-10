package com.becajava.transactionservice.domain.model;

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
