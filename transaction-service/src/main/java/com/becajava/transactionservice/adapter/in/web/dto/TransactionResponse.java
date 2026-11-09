package com.becajava.transactionservice.adapter.in.web.dto;

import com.becajava.transactionservice.domain.model.TransactionCategory;
import com.becajava.transactionservice.domain.model.TransactionStatus;
import com.becajava.transactionservice.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
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

    public TransactionResponse(Long id, Long ownerUserId,
                               Long destinationUserId, BigDecimal amount,
                               LocalDateTime occurredAt, String description,
                               TransactionType type, TransactionCategory category,
                               TransactionStatus status, String rejectionReason,
                               Long reversedTransactionId) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.destinationUserId = destinationUserId;
        this.amount = amount;
        this.occurredAt = occurredAt;
        this.description = description;
        this.type = type;
        this.category = category;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.reversedTransactionId = reversedTransactionId;
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

    public TransactionType getType() {
        return type;
    }

    public TransactionCategory getCategory() {
        return category;
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
