package io.github.douglasdreer.order.domain.valueobject;

/**
 * Enum que representa os possíveis estados de um pedido.
 * 
 * Fluxo: RECEIVED → PROCESSING → CALCULATED → AVAILABLE
 *        ↓
 *      FAILED (em caso de erro)
 */
public enum OrderStatus {
    
    /**
     * Pedido recebido do sistema externo, aguardando processamento.
     */
    RECEIVED("Recebido"),
    
    /**
     * Pedido em processamento (cálculo de totais).
     */
    PROCESSING("Em Processamento"),
    
    /**
     * Pedido com totais calculados.
     */
    CALCULATED("Calculado"),
    
    /**
     * Pedido disponível para consulta pelo Produto Externo B.
     */
    AVAILABLE("Disponível"),
    
    /**
     * Pedido com falha no processamento.
     */
    FAILED("Falhou");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Verifica se o pedido pode transicionar para o próximo status.
     */
    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case RECEIVED -> newStatus == PROCESSING || newStatus == FAILED;
            case PROCESSING -> newStatus == CALCULATED || newStatus == FAILED;
            case CALCULATED -> newStatus == AVAILABLE || newStatus == FAILED;
            case AVAILABLE, FAILED -> false;
        };
    }
}
