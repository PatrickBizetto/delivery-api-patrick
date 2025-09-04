package com.delivery_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura erros de validação (@Valid) e formata a resposta
     * com detalhes de cada campo que falhou.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Erro de validação nos dados enviados",
                request.getDescription(false).replace("uri=", "")
        );
        errorResponse.setErrorCode("VALIDATION_ERROR");
        errorResponse.setDetails(errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Captura a exceção quando uma entidade não é encontrada no banco de dados.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        errorResponse.setErrorCode(ex.getErrorCode());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Captura exceções de conflito, como tentar criar um recurso que já existe.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException ex, WebRequest request) {

        Map<String, String> details = new HashMap<>();
        if (ex.getConflictField() != null && ex.getConflictValue() != null) {
            details.put(ex.getConflictField(), ex.getConflictValue().toString());
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        errorResponse.setErrorCode(ex.getErrorCode());
        errorResponse.setDetails(details.isEmpty() ? null : details);

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Captura todas as outras exceções não tratadas para evitar que a aplicação quebre.
     * Retorna uma mensagem genérica de erro 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        // Logar a exceção real no servidor é uma boa prática aqui
        // ex.printStackTrace();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.",
                request.getDescription(false).replace("uri=", "")
        );
        errorResponse.setErrorCode("INTERNAL_ERROR");

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}