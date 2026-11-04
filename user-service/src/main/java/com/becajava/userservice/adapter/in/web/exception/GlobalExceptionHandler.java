package com.becajava.userservice.adapter.in.web.exception;

import com.becajava.userservice.adapter.in.web.dto.ApiErrorResponse;
import com.becajava.userservice.domain.exception.EmailAlreadyExistsException;
import com.becajava.userservice.domain.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // tentativa de cadastrar ou alterar para um e-mail já utilizado.
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                Map.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    // usuário solicitado não existe.
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundById(
            UserNotFoundException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                Map.of()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    // DTO recebido, mas seus campos violam o Bean Validation.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.putIfAbsent(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Existem campos inválidos",
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // Ocorre quando o corpo da requisição contém um JSON quebrado ou incompleto.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException exception) {
        ApiErrorResponse response =
                new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
                        "Corpo da requisição inválido ou malformado", Map.of());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Ocorre quando um parâmetro não pode ser convertido para o tipo esperado, como "abc" para Long.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        ApiErrorResponse response =
                new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
                        "O parâmetro informado possui um formato inválido", Map.of());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // banco recusou a operação por uma regra de integridade.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        ApiErrorResponse response =
                new ApiErrorResponse(HttpStatus.CONFLICT.value(), "Conflito de integridade nos dados", Map.of());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // URL solicitada não corresponde a nenhum recurso.
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(NoResourceFoundException exception) {
        ApiErrorResponse response =
                new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), "Recurso não encontrado", Map.of());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Ocorre quando a rota existe, mas não aceita o método HTTP utilizado.
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        ApiErrorResponse response =
                new ApiErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), "Método HTTP não permitido para este recurso", Map.of());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    // Captura falhas inesperadas que não foram tratadas pelos handlers específicos.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception exception) {
        ApiErrorResponse response =
                new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ocorreu um erro interno no servidor", Map.of());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}