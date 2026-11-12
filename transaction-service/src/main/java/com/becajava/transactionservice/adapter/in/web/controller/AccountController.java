package com.becajava.transactionservice.adapter.in.web.controller;

import com.becajava.transactionservice.adapter.in.web.dto.AccountBalanceResponse;
import com.becajava.transactionservice.adapter.in.web.mapper.AccountBalanceWebMapper;
import com.becajava.transactionservice.application.model.AccountBalance;
import com.becajava.transactionservice.application.model.AuthenticatedUser;
import com.becajava.transactionservice.port.in.GetAccountBalanceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accounts", description = "Consulta de saldo e limites da conta")
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final GetAccountBalanceUseCase getAccountBalanceUseCase;

    public AccountController(GetAccountBalanceUseCase getAccountBalanceUseCase) {
        this.getAccountBalanceUseCase = getAccountBalanceUseCase;
    }

    @Operation(summary = "Consultar saldo e limites", description = "Consulta o saldo e os limites financeiros do usuário autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo e limites consultados"),
            @ApiResponse(responseCode = "401", description = "Credenciais ausentes ou inválidas"),
            @ApiResponse(responseCode = "404", description = "Conta de saldo e limites não encontrada"),
            @ApiResponse(responseCode = "503", description = "Serviço de saldo e limites indisponível")
    })
    @GetMapping("/balance")
    public ResponseEntity<AccountBalanceResponse> getBalance(Authentication authentication) {

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        AccountBalance accountBalance = getAccountBalanceUseCase.getByUserId(authenticatedUser.getId());

        AccountBalanceResponse response = AccountBalanceWebMapper.toResponse(accountBalance);

        return ResponseEntity.ok(response);
    }
}