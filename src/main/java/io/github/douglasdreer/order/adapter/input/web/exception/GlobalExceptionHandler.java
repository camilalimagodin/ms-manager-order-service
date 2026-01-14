package io.github.douglasdreer.order.adapter.input.web.exception;

import io.github.douglasdreer.order.domain.exception.DomainException;
import io.github.douglasdreer.order.domain.exception.DuplicateOrderException;
import io.github.douglasdreer.order.domain.exception.OrderNotFoundException;
import io.github.douglasdreer.order.domain.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Tratamento global de exceções da API REST.
 * Converte exceções de domínio em respostas HTTP apropriadas seguindo RFC 7807.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Trata exceções de validação de domínio.
     */
    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException ex) {
        log.warn("Erro de validação: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
        
        problemDetail.setTitle("Erro de Validação");
        problemDetail.setType(URI.create("https://api.order-service.io/errors/validation"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return problemDetail;
    }

    /**
     * Trata exceções de pedido não encontrado.
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleOrderNotFoundException(OrderNotFoundException ex) {
        log.warn("Pedido não encontrado: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        
        problemDetail.setTitle("Pedido Não Encontrado");
        problemDetail.setType(URI.create("https://api.order-service.io/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return problemDetail;
    }

    /**
     * Trata exceções de pedido duplicado.
     */
    @ExceptionHandler(DuplicateOrderException.class)
    public ProblemDetail handleDuplicateOrderException(DuplicateOrderException ex) {
        log.warn("Pedido duplicado: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage());
        
        problemDetail.setTitle("Pedido Duplicado");
        problemDetail.setType(URI.create("https://api.order-service.io/errors/duplicate"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return problemDetail;
    }

    /**
     * Trata exceções de validação do Bean Validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("Erro de validação de entrada: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Dados de entrada inválidos");
        
        problemDetail.setTitle("Erro de Validação de Entrada");
        problemDetail.setType(URI.create("https://api.order-service.io/errors/validation"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errors", errors);
        
        return problemDetail;
    }

    /**
     * Trata transições de status inválidas.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalStateException(IllegalStateException ex) {
        log.warn("Estado inválido: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage());
        
        problemDetail.setTitle("Estado Inválido");
        problemDetail.setType(URI.create("https://api.order-service.io/errors/invalid-state"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return problemDetail;
    }

    /**
     * Trata outras exceções de domínio.
     */
    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        log.error("Erro de domínio: {}", ex.getMessage(), ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
        
        problemDetail.setTitle("Erro de Domínio");
        problemDetail.setType(URI.create("https://api.order-service.io/errors/domain"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return problemDetail;
    }

    /**
     * Trata exceções não mapeadas.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Erro interno do servidor: {}", ex.getMessage(), ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor. Contate o suporte.");
        
        problemDetail.setTitle("Erro Interno");
        problemDetail.setType(URI.create("https://api.order-service.io/errors/internal"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return problemDetail;
    }
}
