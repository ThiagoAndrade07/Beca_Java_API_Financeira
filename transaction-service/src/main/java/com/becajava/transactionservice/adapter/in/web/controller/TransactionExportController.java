package com.becajava.transactionservice.adapter.in.web.controller;

import com.becajava.transactionservice.application.model.AuthenticatedUser;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.in.ExportTransactionsCsvUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(
        name = "Reports",
        description = "Exportação de relatórios financeiros"
)
@RestController
@RequestMapping("/transactions")
public class TransactionExportController {

    private final ExportTransactionsCsvUseCase exportTransactionsCsvUseCase;

    public TransactionExportController(
            ExportTransactionsCsvUseCase exportTransactionsCsvUseCase
    ) {
        this.exportTransactionsCsvUseCase =
                exportTransactionsCsvUseCase;
    }

    @Operation(
            summary = "Exportar transações em CSV",
            description = "Exporta as transações do usuário autenticado, "
                    + "com filtros opcionais por tipo e período."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arquivo CSV gerado"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais ausentes ou inválidas")
    })
    @GetMapping(
            value = "/export/csv",
            produces = "text/csv"
    )
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false)
            TransactionType type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            Authentication authentication
    ) {
        AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) authentication.getPrincipal();

        byte[] csv =
                exportTransactionsCsvUseCase.export(
                        authenticatedUser.getId(),
                        type,
                        startDate,
                        endDate
                );

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"transactions.csv\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                "text/csv;charset=UTF-8"
                        )
                )
                .body(csv);
    }
}