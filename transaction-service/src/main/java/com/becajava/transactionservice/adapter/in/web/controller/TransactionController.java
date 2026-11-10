package com.becajava.transactionservice.adapter.in.web.controller;

import com.becajava.transactionservice.port.in.CreateTransactionUseCase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.becajava.transactionservice.adapter.in.web.dto.CreateTransactionRequest;
import com.becajava.transactionservice.adapter.in.web.dto.TransactionResponse;
import com.becajava.transactionservice.adapter.in.web.mapper.TransactionWebMapper;
import com.becajava.transactionservice.application.model.AuthenticatedUser;
import com.becajava.transactionservice.application.model.CreateTransactionCommand;
import com.becajava.transactionservice.domain.model.Transaction;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request, Authentication authentication) {

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        CreateTransactionCommand command =
                new CreateTransactionCommand(
                        authenticatedUser.getId(),
                        request.getAmount(),
                        request.getDescription(),
                        request.getCategory(),
                        request.getType(),
                        request.getDestinationUserId()
                );

        Transaction createdTransaction = createTransactionUseCase.createTransaction(command);

        TransactionResponse response = TransactionWebMapper.toResponse(createdTransaction);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
