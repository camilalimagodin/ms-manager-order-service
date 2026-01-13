package io.github.douglasdreer.order.domain.exception;

import io.github.douglasdreer.order.domain.valueobject.OrderStatus;

/**
 * Exceção lançada quando há tentativa de transição de status inválida.
 */
public class InvalidOrderStatusTransitionException extends DomainException {
    
    public InvalidOrderStatusTransitionException(OrderStatus from, OrderStatus to) {
        super("Transição de status inválida: %s → %s".formatted(from, to));
    }
}
