package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.application.model.AccountBalance;
import com.becajava.transactionservice.application.model.CreateTransactionCommand;
import com.becajava.transactionservice.domain.exception.InvalidTransactionException;
import com.becajava.transactionservice.domain.model.Transaction;
import com.becajava.transactionservice.domain.model.TransactionType;
import com.becajava.transactionservice.port.in.CreateTransactionUseCase;
import com.becajava.transactionservice.port.out.AccountBalancePort;
import com.becajava.transactionservice.port.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;


@Service
public class CreateTransactionService implements CreateTransactionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    private final AccountBalancePort accountBalancePort;

    public CreateTransactionService(TransactionRepositoryPort transactionRepositoryPort, AccountBalancePort accountBalancePort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.accountBalancePort = accountBalancePort;
    }

    @Override
    public Transaction createTransaction(CreateTransactionCommand command) {
        Transaction transaction;

        if (TransactionType.DEPOSIT.equals(command.getType())) {
            transaction = Transaction.createDeposit(
                    command.getOwnerUserId(),
                    command.getAmount(),
                    command.getDescription(),
                    command.getCategory()
            );

        } else if (TransactionType.WITHDRAWAL.equals(command.getType())) {
            transaction = createWithdrawal(command);

        } else if (TransactionType.TRANSFER.equals(command.getType())) {
            transaction = createTransfer(command);

        } else if (TransactionType.PURCHASE.equals(command.getType())) {
            transaction = createPurchase(command);

        } else {
            throw new InvalidTransactionException(
                    "Tipo de transação ainda não suportado"
            );
        }

        return transactionRepositoryPort.save(transaction);
    }

    private Transaction createWithdrawal(CreateTransactionCommand command) {

        AccountBalance accountBalance =
                accountBalancePort
                        .findByUserId(command.getOwnerUserId())
                        .orElseThrow(
                                () -> new InvalidTransactionException(
                                        "Conta de saldo e limites não encontrada"
                                )
                        );

        LocalDate today = LocalDate.now(SAO_PAULO);

        LocalDateTime startOfDay = today.atStartOfDay();

        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();

        BigDecimal spentToday =
                transactionRepositoryPort
                        .sumApprovedOutgoingAmountByOwnerUserIdBetween(
                                command.getOwnerUserId(),
                                startOfDay,
                                startOfNextDay
                        );

        return Transaction.createWithdrawal(
                command.getOwnerUserId(),
                command.getAmount(),
                command.getDescription(),
                command.getCategory(),
                accountBalance.getBalance(),
                accountBalance.getTransactionLimit(),
                accountBalance.getDailyLimit(),
                spentToday
        );
    }

    private Transaction createTransfer(CreateTransactionCommand command) {
        AccountBalance accountBalance =
                accountBalancePort
                        .findByUserId(command.getOwnerUserId())
                        .orElseThrow(
                                () -> new InvalidTransactionException(
                                        "Conta de saldo e limites não encontrada"
                                )
                        );

        LocalDate today = LocalDate.now(SAO_PAULO);

        LocalDateTime startOfDay = today.atStartOfDay();

        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();

        BigDecimal spentToday =
                transactionRepositoryPort
                        .sumApprovedOutgoingAmountByOwnerUserIdBetween(
                                command.getOwnerUserId(),
                                startOfDay,
                                startOfNextDay
                        );

        return Transaction.createTransfer(
                command.getOwnerUserId(),
                command.getDestinationUserId(),
                command.getAmount(),
                command.getDescription(),
                accountBalance.getBalance(),
                accountBalance.getTransactionLimit(),
                accountBalance.getDailyLimit(),
                spentToday
        );
    }

    private Transaction createPurchase(CreateTransactionCommand command) {
        AccountBalance accountBalance =
                accountBalancePort
                        .findByUserId(command.getOwnerUserId())
                        .orElseThrow(
                                () -> new InvalidTransactionException(
                                        "Conta de saldo e limites não encontrada"
                                )
                        );

        LocalDate today = LocalDate.now(SAO_PAULO);

        LocalDateTime startOfDay = today.atStartOfDay();

        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();

        BigDecimal spentToday =
                transactionRepositoryPort
                        .sumApprovedOutgoingAmountByOwnerUserIdBetween(
                                command.getOwnerUserId(),
                                startOfDay,
                                startOfNextDay
                        );

        return Transaction.createPurchase(
                command.getOwnerUserId(),
                command.getAmount(),
                command.getDescription(),
                command.getCategory(),
                accountBalance.getBalance(),
                accountBalance.getTransactionLimit(),
                accountBalance.getDailyLimit(),
                spentToday
        );
    }
}

