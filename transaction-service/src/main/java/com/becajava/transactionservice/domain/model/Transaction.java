package com.becajava.transactionservice.domain.model;

import com.becajava.transactionservice.domain.exception.InvalidTransactionException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

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


    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");

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
                LocalDateTime.now(SAO_PAULO),
                description.trim(),
                category,
                TransactionType.DEPOSIT,
                TransactionStatus.APPROVED,
                null,
                null
        );
    }

    public static Transaction createWithdrawal(
            Long ownerUserId,
            BigDecimal amount,
            String description,
            TransactionCategory category,
            BigDecimal balance,
            BigDecimal transactionLimit,
            BigDecimal dailyLimit,
            BigDecimal spentToday
    ) {
        if (ownerUserId == null) {
            throw new InvalidTransactionException(
                    "O usuário dono da transação é obrigatório"
            );
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException(
                    "O valor da retirada deve ser maior que zero"
            );
        }

        if (description == null || description.isBlank()) {
            throw new InvalidTransactionException(
                    "A descrição da retirada é obrigatória"
            );
        }

        if (category == null || category == TransactionCategory.INCOME) {
            throw new InvalidTransactionException(
                    "A retirada deve utilizar uma categoria de despesa"
            );
        }

        String rejectionReason = null;

        if (amount.compareTo(balance) > 0) {
            rejectionReason = "Saldo insuficiente";

        } else if (amount.compareTo(transactionLimit) > 0) {
            rejectionReason = "Valor acima do limite por transação";

        } else if (spentToday.add(amount).compareTo(dailyLimit) > 0) {
            rejectionReason = "Limite diário excedido";
        }

        TransactionStatus status = rejectionReason == null ? TransactionStatus.APPROVED : TransactionStatus.REJECTED;

        return new Transaction(
                ownerUserId,
                null,
                amount,
                LocalDateTime.now(SAO_PAULO),
                description.trim(),
                category,
                TransactionType.WITHDRAWAL,
                status,
                rejectionReason,
                null
        );
    }

    public static Transaction createTransfer(
            Long ownerUserId,
            Long destinationUserId,
            BigDecimal amount,
            String description,
            BigDecimal balance,
            BigDecimal transactionLimit,
            BigDecimal dailyLimit,
            BigDecimal spentToday
    ) {
        if (ownerUserId == null) {
            throw new InvalidTransactionException(
                    "O usuário remetente é obrigatório"
            );
        }

        if (destinationUserId == null) {
            throw new InvalidTransactionException(
                    "O usuário destinatário é obrigatório"
            );
        }

        if (ownerUserId.equals(destinationUserId)) {
            throw new InvalidTransactionException(
                    "O destinatário deve ser diferente do remetente"
            );
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException(
                    "O valor da transferência deve ser maior que zero"
            );
        }

        if (description == null || description.isBlank()) {
            throw new InvalidTransactionException(
                    "A descrição da transferência é obrigatória"
            );
        }

        String rejectionReason = null;

        if (amount.compareTo(balance) > 0) {
            rejectionReason = "Saldo insuficiente";

        } else if (amount.compareTo(transactionLimit) > 0) {
            rejectionReason = "Valor acima do limite por transação";

        } else if (spentToday.add(amount).compareTo(dailyLimit) > 0) {
            rejectionReason = "Limite diário excedido";
        }

        TransactionStatus status = rejectionReason == null ? TransactionStatus.APPROVED : TransactionStatus.REJECTED;

        return new Transaction(
                ownerUserId,
                destinationUserId,
                amount,
                LocalDateTime.now(SAO_PAULO),
                description.trim(),
                TransactionCategory.TRANSFER,
                TransactionType.TRANSFER,
                status,
                rejectionReason,
                null
        );
    }

    public static Transaction createPurchase(
            Long ownerUserId,
            BigDecimal amount,
            String description,
            TransactionCategory category,
            BigDecimal balance,
            BigDecimal transactionLimit,
            BigDecimal dailyLimit,
            BigDecimal spentToday
    ) {
        if (ownerUserId == null) {
            throw new InvalidTransactionException("O usuário dono da compra é obrigatório");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("O valor da compra deve ser maior que zero");
        }

        if (description == null || description.isBlank()) {
            throw new InvalidTransactionException("A descrição da compra é obrigatória");
        }

        if (
                category == null
                        || category == TransactionCategory.INCOME
                        || category == TransactionCategory.TRANSFER
        ) {
            throw new InvalidTransactionException("A compra deve utilizar uma categoria de despesa");
        }

        String rejectionReason = null;

        if (amount.compareTo(balance) > 0) {
            rejectionReason = "Saldo insuficiente";

        } else if (amount.compareTo(transactionLimit) > 0) {
            rejectionReason = "Valor acima do limite por transação";

        } else if (spentToday.add(amount).compareTo(dailyLimit) > 0) {
            rejectionReason = "Limite diário excedido";
        }

        TransactionStatus status = rejectionReason == null ? TransactionStatus.APPROVED : TransactionStatus.REJECTED;

        return new Transaction(
                ownerUserId,
                null,
                amount,
                LocalDateTime.now(SAO_PAULO),
                description.trim(),
                category,
                TransactionType.PURCHASE,
                status,
                rejectionReason,
                null
        );
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
