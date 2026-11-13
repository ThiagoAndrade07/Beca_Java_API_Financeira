package com.becajava.transactionservice.application.service;

import com.becajava.transactionservice.application.exception.ExchangeRateNotFoundException;
import com.becajava.transactionservice.application.exception.ExchangeServiceUnavailableException;
import com.becajava.transactionservice.application.model.AccountBalance;
import com.becajava.transactionservice.application.model.CreateTransactionCommand;
import com.becajava.transactionservice.application.model.ExchangeRateResult;
import com.becajava.transactionservice.domain.exception.InvalidTransactionException;
import com.becajava.transactionservice.domain.model.*;
import com.becajava.transactionservice.port.in.CreateTransactionUseCase;
import com.becajava.transactionservice.port.out.AccountBalancePort;
import com.becajava.transactionservice.port.out.ExchangeRatePort;
import com.becajava.transactionservice.port.out.TransactionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;


@Service
public class CreateTransactionService implements CreateTransactionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;

    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateTransactionService.class);

    private final AccountBalancePort accountBalancePort;
    private final ExchangeRatePort exchangeRatePort;

    public CreateTransactionService(TransactionRepositoryPort transactionRepositoryPort, AccountBalancePort accountBalancePort, ExchangeRatePort exchangeRatePort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.accountBalancePort = accountBalancePort;
        this.exchangeRatePort = exchangeRatePort;
    }

    @Override
    public Transaction createTransaction(CreateTransactionCommand command) {
        Transaction transaction;

        if (TransactionType.DEPOSIT.equals(command.getType())) {
            transaction = createDeposit(command);

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

        attachExchangeSnapshot(transaction);

        return transactionRepositoryPort.save(transaction);
    }

    private void attachExchangeSnapshot(Transaction transaction) {

        LocalDate transactionDate = transaction.getOccurredAt().toLocalDate();

        try {
            ExchangeRateResult usdRate = exchangeRatePort.getSellingRate("USD", transactionDate);

            ExchangeRateResult eurRate = exchangeRatePort.getSellingRate("EUR", transactionDate);

            ExchangeQuote usd = ExchangeQuote.fromBrl("USD", transaction.getAmount(), usdRate.getSellingRate(), usdRate.getQuoteDate());

            ExchangeQuote eur = ExchangeQuote.fromBrl("EUR", transaction.getAmount(), eurRate.getSellingRate(), eurRate.getQuoteDate());

            transaction.attachExchangeSnapshot(ExchangeSnapshot.available(usd, eur));

        } catch (
                ExchangeRateNotFoundException | ExchangeServiceUnavailableException exception
        ) {
            LOGGER.warn("Conversão indisponível para a transação do usuário {}: {}", transaction.getOwnerUserId(), exception.getMessage());

            transaction.attachExchangeSnapshot(ExchangeSnapshot.unavailable("Conversão indisponível"));
        }
    }

    private Transaction createDeposit(CreateTransactionCommand command) {
        AccountBalance accountBalance =
                accountBalancePort.findByUserId(command.getOwnerUserId())
                        .orElseThrow(
                                () -> new InvalidTransactionException(
                                        "Conta de saldo e limites não encontrada"
                                )
                        );

        Transaction transaction = Transaction.createDeposit(
                command.getOwnerUserId(),
                command.getAmount(),
                command.getDescription(),
                command.getCategory()
        );

        BigDecimal newBalance = accountBalance.getBalance().add(command.getAmount());

        accountBalancePort.updateBalance(command.getOwnerUserId(), newBalance);

        return transaction;
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

        Transaction transaction = Transaction.createWithdrawal(
                command.getOwnerUserId(),
                command.getAmount(),
                command.getDescription(),
                command.getCategory(),
                accountBalance.getBalance(),
                accountBalance.getTransactionLimit(),
                accountBalance.getDailyLimit(),
                spentToday
        );

        if (TransactionStatus.APPROVED.equals(transaction.getStatus())) {
            BigDecimal newBalance = accountBalance.getBalance().subtract(command.getAmount());

            accountBalancePort.updateBalance(command.getOwnerUserId(), newBalance);
        }

        return transaction;
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

        Transaction transaction = Transaction.createTransfer(
                command.getOwnerUserId(),
                command.getDestinationUserId(),
                command.getAmount(),
                command.getDescription(),
                accountBalance.getBalance(),
                accountBalance.getTransactionLimit(),
                accountBalance.getDailyLimit(),
                spentToday
        );

        if (TransactionStatus.APPROVED.equals(transaction.getStatus())) {
            AccountBalance destinationAccount =
                    accountBalancePort
                            .findByUserId(command.getDestinationUserId())
                            .orElseThrow(
                                    () -> new InvalidTransactionException(
                                            "Conta do usuário destinatário não encontrada"
                                    )
                            );

            BigDecimal newOwnerBalance = accountBalance.getBalance().subtract(command.getAmount());

            BigDecimal newDestinationBalance = destinationAccount.getBalance().add(command.getAmount());

            accountBalancePort.updateBalance(command.getOwnerUserId(), newOwnerBalance);

            accountBalancePort.updateBalance(command.getDestinationUserId(), newDestinationBalance);
        }

        return transaction;
    }

    private Transaction createPurchase(
            CreateTransactionCommand command
    ) {
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

        LocalDateTime startOfNextDay =
                today.plusDays(1).atStartOfDay();

        BigDecimal spentToday =
                transactionRepositoryPort
                        .sumApprovedOutgoingAmountByOwnerUserIdBetween(
                                command.getOwnerUserId(),
                                startOfDay,
                                startOfNextDay
                        );

        Transaction transaction = Transaction.createPurchase(
                command.getOwnerUserId(),
                command.getAmount(),
                command.getDescription(),
                command.getCategory(),
                accountBalance.getBalance(),
                accountBalance.getTransactionLimit(),
                accountBalance.getDailyLimit(),
                spentToday
        );

        if (
                TransactionStatus.APPROVED.equals(
                        transaction.getStatus()
                )
        ) {
            BigDecimal newBalance = accountBalance
                    .getBalance()
                    .subtract(command.getAmount());

            accountBalancePort.updateBalance(
                    command.getOwnerUserId(),
                    newBalance
            );
        }

        return transaction;
    }
}
