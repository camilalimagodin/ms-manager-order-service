package io.github.douglasdreer.order.infrastructure.persistence.entity;

/**
 * Enum que representa os status do pedido na camada de persistência.
 * Espelha o enum de domínio para uso nas entidades JPA.
 */
public enum OrderStatusEntity {
    
    /**
     * Pedido recebido do sistema externo.
     */
    RECEIVED,
    
    /**
     * Pedido em processamento.
     */
    PROCESSING,
    
    /**
     * Pedido com totais calculados.
     */
    CALCULATED,
    
    /**
     * Pedido disponível para consulta.
     */
    AVAILABLE,
    
    /**
     * Pedido com erro no processamento.
     */
    FAILED
}
