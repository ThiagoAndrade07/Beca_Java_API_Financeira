package com.becajava.transactionservice.adapter.in.web.controller;

import com.becajava.transactionservice.adapter.in.web.dto.ExpenseAnalysisResponse;
import com.becajava.transactionservice.adapter.in.web.mapper.ExpenseAnalysisWebMapper;
import com.becajava.transactionservice.application.model.AuthenticatedUser;
import com.becajava.transactionservice.application.model.ExpenseAnalysisResult;
import com.becajava.transactionservice.application.model.ExpenseGrouping;
import com.becajava.transactionservice.port.in.AnalyzeExpensesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(
        name = "Expense analysis",
        description = "Análise de despesas financeiras"
)
@RestController
@RequestMapping("/transactions/analysis")
public class ExpenseAnalysisController {

    private final AnalyzeExpensesUseCase analyzeExpensesUseCase;

    public ExpenseAnalysisController(
            AnalyzeExpensesUseCase analyzeExpensesUseCase
    ) {
        this.analyzeExpensesUseCase = analyzeExpensesUseCase;
    }

    @Operation(
            summary = "Analisar despesas",
            description = """
                    Agrupa as despesas aprovadas do usuário autenticado
                    por categoria, dia ou mês. Quando o período não é
                    informado, considera o mês atual no fuso de São Paulo.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Análise gerada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetros inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais ausentes ou inválidas"
            )
    })
    @GetMapping("/expenses")
    public ResponseEntity<ExpenseAnalysisResponse> analyzeExpenses(
            @RequestParam
            ExpenseGrouping groupBy,

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

        ExpenseAnalysisResult result =
                analyzeExpensesUseCase.analyze(
                        authenticatedUser.getId(),
                        groupBy,
                        startDate,
                        endDate
                );

        ExpenseAnalysisResponse response =
                ExpenseAnalysisWebMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }
}