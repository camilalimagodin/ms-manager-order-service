package io.github.douglasdreer.order.domain.exception;

/**
 * Exceção lançada quando há erro de validação de dados.
 */
public class ValidationException extends DomainException {
    
    public ValidationException(String message) {
        super(message);
    }
}
