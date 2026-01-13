package com.order.domain.exception;

/**
 * Exceção lançada quando há tentativa de criar pedido duplicado.
 */
public class DuplicateOrderException extends DomainException {
    
    public DuplicateOrderException(String externalOrderId) {
        super("Pedido já existe com External ID: " + externalOrderId);
    }
}
