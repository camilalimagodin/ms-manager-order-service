package com.order.domain.exception;

import com.order.domain.valueobject.OrderStatus;

/**
 * Exceção lançada quando há tentativa de transição de status inválida.
 */
public class InvalidOrderStatusTransitionException extends DomainException {
    
    public InvalidOrderStatusTransitionException(OrderStatus from, OrderStatus to) {
        super(String.format("Transição de status inválida: %s → %s", from, to));
    }
}
