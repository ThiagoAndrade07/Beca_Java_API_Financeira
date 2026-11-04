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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import com.becajava.userservice.application.exception.InvalidCsvFileException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    // Ocorre quando a requisição utiliza um Content-Type que o endpoint não aceita.
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Content-Type não suportado para este recurso", Map.of()
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    // Ocorre quando um parâmetro obrigatório não é enviado na requisição.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingRequestParameter(
            MissingServletRequestParameterException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Parâmetro obrigatório não informado: "
                        + exception.getParameterName(),
                Map.of()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // Ocorre quando uma parte obrigatória de uma requisição multipart, como um arquivo, não é enviada.
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingRequestPart(
            MissingServletRequestPartException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Parte obrigatória não informada: "
                        + exception.getRequestPartName(),
                Map.of()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // Ocorre quando o arquivo CSV está vazio, sem usuários ou possui cabeçalho inválido.
    @ExceptionHandler(InvalidCsvFileException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCsvFile(
            InvalidCsvFileException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                Map.of()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // Ocorre quando há uma falha técnica durante a leitura do arquivo CSV.
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiErrorResponse> handleCsvReadError(
            IOException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "O arquivo CSV não pôde ser processado",
                Map.of()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // Captura falhas inesperadas que não foram tratadas pelos handlers específicos.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception exception) {

        log.error("Erro inesperado ao processar a requisição", exception);

        ApiErrorResponse response =
                new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ocorreu um erro interno no servidor", Map.of());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}