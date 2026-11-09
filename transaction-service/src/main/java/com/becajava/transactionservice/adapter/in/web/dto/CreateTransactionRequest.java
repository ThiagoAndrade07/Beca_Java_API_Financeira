package com.becajava.transactionservice.adapter.in.web.dto;

import com.becajava.transactionservice.domain.model.TransactionCategory;
import com.becajava.transactionservice.domain.model.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CreateTransactionRequest {

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Digits(integer = 17, fraction = 2, message = "O valor deve ter no máximo 17 dígitos inteiros e 2 casas decimais")
    private BigDecimal amount;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String description;

    @NotNull(message = "A categoria é obrigatória")
    private TransactionCategory category;

    @NotNull(message = "O tipo da transação é obrigatório")
    private TransactionType type;

    private Long destinationUserId;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionCategory getCategory() {
        return category;
    }

    public void setCategory(TransactionCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDestinationUserId() {
        return destinationUserId;
    }

    public void setDestinationUserId(Long destinationUserId) {
        this.destinationUserId = destinationUserId;
    }
}
