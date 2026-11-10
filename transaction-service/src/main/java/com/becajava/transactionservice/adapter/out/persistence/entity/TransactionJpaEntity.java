package com.becajava.transactionservice.adapter.out.persistence.entity;

import com.becajava.transactionservice.domain.model.TransactionCategory;
import com.becajava.transactionservice.domain.model.TransactionStatus;
import com.becajava.transactionservice.domain.model.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class TransactionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(name = "destination_user_id")
    private Long destinationUserId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(nullable = false, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    @Column(name = "reversed_transaction_id")
    private Long reversedTransactionId;

    protected TransactionJpaEntity() {
    }

    public TransactionJpaEntity(
            Long ownerUserId,
            Long destinationUserId,
            BigDecimal amount,
            LocalDateTime occurredAt,
            String description,
            TransactionCategory category,
            TransactionType type,
            TransactionStatus status,
            String rejectionReason,
            Long reversedTransactionId
    ) {
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

    public TransactionJpaEntity(
            Long id,
            Long ownerUserId,
            Long destinationUserId,
            BigDecimal amount,
            LocalDateTime occurredAt,
            String description,
            TransactionCategory category,
            TransactionType type,
            TransactionStatus status,
            String rejectionReason,
            Long reversedTransactionId
    ) {
        this(
                ownerUserId,
                destinationUserId,
                amount,
                occurredAt,
                description,
                category,
                type,
                status,
                rejectionReason,
                reversedTransactionId
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

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
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