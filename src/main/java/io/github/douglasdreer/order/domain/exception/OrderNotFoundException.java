package io.github.douglasdreer.order.domain.exception;

/**
 * Exceção lançada quando um pedido não é encontrado.
 */
public class OrderNotFoundException extends DomainException {
    
    public OrderNotFoundException(String message) {
        super(message);
    }
    
    public static OrderNotFoundException byId(String id) {
        return new OrderNotFoundException("Pedido não encontrado com ID: " + id);
    }
    
    public static OrderNotFoundException byExternalId(String externalId) {
        return new OrderNotFoundException("Pedido não encontrado com External ID: " + externalId);
    }
}
