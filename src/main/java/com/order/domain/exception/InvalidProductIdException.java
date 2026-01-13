package com.order.domain.exception;

/**
 * Exceção lançada quando um ProductId é inválido.
 */
public class InvalidProductIdException extends DomainException {
    
    public InvalidProductIdException(String message) {
        super(message);
    }
}
