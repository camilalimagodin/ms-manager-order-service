package com.order.domain.exception;

/**
 * Exceção lançada quando um ExternalOrderId é inválido.
 */
public class InvalidExternalOrderIdException extends DomainException {
    
    public InvalidExternalOrderIdException(String message) {
        super(message);
    }
}
