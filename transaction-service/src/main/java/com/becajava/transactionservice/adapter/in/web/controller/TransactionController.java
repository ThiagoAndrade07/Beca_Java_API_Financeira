package com.becajava.transactionservice.adapter.in.web.controller;

import com.becajava.transactionservice.adapter.in.web.dto.CreateTransactionRequest;
import com.becajava.transactionservice.adapter.in.web.dto.TransactionResponse;
import com.becajava.transactionservice.adapter.in.web.mapper.TransactionWebMapper;
import com.becajava.transactionservice.application.model.AuthenticatedUser;
import com.becajava.transactionservice.application.model.CreateTransactionCommand;
import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.in.CreateTransactionUseCase;
import com.becajava.transactionservice.port.in.GetTransactionByIdUseCase;
import com.becajava.transactionservice.port.in.ListTransactionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "Transactions", description = "Operações de transações financeiras")
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionByIdUseCase getTransactionByIdUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase,
                                 GetTransactionByIdUseCase getTransactionByIdUseCase,
                                 ListTransactionsUseCase listTransactionsUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.getTransactionByIdUseCase = getTransactionByIdUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
    }

    @Operation(
            summary = "Criar transação",
            description = "Cria depósitos, retiradas, transferências e compras "
                    + "para o usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transação criada"),
            @ApiResponse(responseCode = "400", description = "Dados ou regra de negócio inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais ausentes ou inválidas"),
            @ApiResponse(responseCode = "503", description = "Serviço externo indisponível")
    })

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

    @Operation(summary = "Listar transações", description = "Lista as transações do usuário autenticado, " + "com filtros opcionais por tipo e período.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transações listadas"),
            @ApiResponse(responseCode = "400", description = "Período ou parâmetro inválido"),
            @ApiResponse(responseCode = "401", description = "Credenciais ausentes ou inválidas")
    })
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> listTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            Authentication authentication
    ) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        List<Transaction> transactions = listTransactionsUseCase.listByOwnerUserId(authenticatedUser.getId(), type, startDate, endDate);

        List<TransactionResponse> responses = new ArrayList<>();

        for (Transaction transaction : transactions) {
            responses.add(TransactionWebMapper.toResponse(transaction));
        }

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Consultar transação por ID", description = "Retorna uma transação pertencente ao usuário autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transação encontrada"),
            @ApiResponse(responseCode = "401", description = "Credenciais ausentes ou inválidas"),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        Transaction transaction = getTransactionByIdUseCase.getById(id, authenticatedUser.getId());

        TransactionResponse response = TransactionWebMapper.toResponse(transaction);

        return ResponseEntity.ok(response);
    }
}
