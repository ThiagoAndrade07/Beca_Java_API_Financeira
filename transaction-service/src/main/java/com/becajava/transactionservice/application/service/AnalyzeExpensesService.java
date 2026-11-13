package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.application.model.ExpenseAnalysisResult;
import com.becajava.transactionservice.application.model.ExpenseGrouping;
import com.becajava.transactionservice.application.model.ExpenseSummary;
import com.becajava.transactionservice.domain.exception.InvalidTransactionException;
import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionStatus;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.in.AnalyzeExpensesUseCase;
import com.becajava.transactionservice.port.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class AnalyzeExpensesService implements AnalyzeExpensesUseCase {

    private static final ZoneId SAO_PAULO =
            ZoneId.of("America/Sao_Paulo");

    private final TransactionRepositoryPort transactionRepositoryPort;

    public AnalyzeExpensesService(
            TransactionRepositoryPort transactionRepositoryPort
    ) {
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Override
    public ExpenseAnalysisResult analyze(
            Long ownerUserId,
            ExpenseGrouping groupBy,
            LocalDate startDate,
            LocalDate endDate
    ) {
        validateParameters(groupBy, startDate, endDate);

        LocalDate effectiveStartDate = startDate;
        LocalDate effectiveEndDate = endDate;

        if (startDate == null && endDate == null) {
            YearMonth currentMonth = YearMonth.now(SAO_PAULO);

            effectiveStartDate = currentMonth.atDay(1);
            effectiveEndDate = currentMonth.atEndOfMonth();
        }

        LocalDateTime start =
                effectiveStartDate != null
                        ? effectiveStartDate.atStartOfDay()
                        : LocalDate.of(1970, 1, 1).atStartOfDay();

        LocalDateTime end =
                effectiveEndDate != null
                        ? effectiveEndDate.plusDays(1).atStartOfDay()
                        : LocalDate.of(9999, 12, 31).atStartOfDay();

        List<Transaction> transactions =
                transactionRepositoryPort
                        .findAllByOwnerUserIdAndFilters(
                                ownerUserId,
                                null,
                                start,
                                end
                        );

        Map<String, BigDecimal> totalsByGroup = new TreeMap<>();

        for (Transaction transaction : transactions) {
            if (!isApprovedExpense(transaction)) {
                continue;
            }

            String group = resolveGroup(transaction, groupBy);

            BigDecimal currentTotal =
                    totalsByGroup.getOrDefault(
                            group,
                            BigDecimal.ZERO
                    );

            totalsByGroup.put(
                    group,
                    currentTotal.add(transaction.getAmount())
            );
        }

        List<ExpenseSummary> summaries = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, BigDecimal> entry : totalsByGroup.entrySet()) {
            summaries.add(
                    new ExpenseSummary(
                            entry.getKey(),
                            entry.getValue()
                    )
            );

            total = total.add(entry.getValue());
        }

        return new ExpenseAnalysisResult(
                summaries,
                total
        );
    }

    private void validateParameters(
            ExpenseGrouping groupBy,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (groupBy == null) {
            throw new InvalidTransactionException(
                    "O tipo de agrupamento é obrigatório"
            );
        }

        if (
                startDate != null
                        && endDate != null
                        && startDate.isAfter(endDate)
        ) {
            throw new InvalidTransactionException(
                    "A data inicial não pode ser posterior à data final"
            );
        }
    }

    private boolean isApprovedExpense(Transaction transaction) {
        if (!TransactionStatus.APPROVED.equals(transaction.getStatus())) {
            return false;
        }

        return TransactionType.PURCHASE.equals(transaction.getType())
                || TransactionType.WITHDRAWAL.equals(transaction.getType())
                || TransactionType.TRANSFER.equals(transaction.getType());
    }

    private String resolveGroup(
            Transaction transaction,
            ExpenseGrouping groupBy
    ) {
        if (ExpenseGrouping.CATEGORY.equals(groupBy)) {
            return transaction.getCategory().name();
        }

        if (ExpenseGrouping.DAY.equals(groupBy)) {
            return transaction
                    .getOccurredAt()
                    .toLocalDate()
                    .toString();
        }

        return YearMonth
                .from(transaction.getOccurredAt())
                .toString();
    }
}