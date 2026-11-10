package com.becajava.transactionservice.adapter.in.web.exception;

import com.becajava.transactionservice.adapter.in.web.dto.ApiErrorResponse;
import com.becajava.transactionservice.domain.exception.InvalidTransactionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidTransaction(InvalidTransactionException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}