package io.github.douglasdreer.order.domain.exception;

/**
 * Exceção base para erros de domínio.
 */
public abstract class DomainException extends RuntimeException {
    
    protected DomainException(String message) {
        super(message);
    }
    
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
