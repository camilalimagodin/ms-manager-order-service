package com.order.domain.exception;

/**
 * Exceção lançada quando um valor monetário é inválido.
 */
public class InvalidMoneyException extends DomainException {
    
    public InvalidMoneyException(String message) {
        super(message);
    }
}
