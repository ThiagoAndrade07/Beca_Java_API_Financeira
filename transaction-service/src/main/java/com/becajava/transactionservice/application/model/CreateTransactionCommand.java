package com.becajava.transactionservice.application.model;

import com.becajava.transactionservice.domain.model.TransactionCategory;
import com.becajava.transactionservice.domain.model.TransactionType;

import java.math.BigDecimal;

public class CreateTransactionCommand {
    private final Long ownerUserId;
    private final BigDecimal amount;
    private final String description;
    private final TransactionCategory category;
    private final TransactionType type;
    private final Long destinationUserId;

    public CreateTransactionCommand(Long ownerUserId, BigDecimal amount, String description,
                                    TransactionCategory category, TransactionType type, Long destinationUserId) {
        this.ownerUserId = ownerUserId;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.type = type;
        this.destinationUserId = destinationUserId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
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

    public Long getDestinationUserId() {
        return destinationUserId;
    }
}
