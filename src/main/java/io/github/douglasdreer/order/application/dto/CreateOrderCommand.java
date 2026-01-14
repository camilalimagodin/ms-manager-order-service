package io.github.douglasdreer.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de entrada para criação de pedido.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderCommand {

    /**
     * Identificador externo do pedido (obrigatório).
     */
    private String externalOrderId;

    /**
     * Lista de itens do pedido.
     */
    private List<OrderItemCommand> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemCommand {
        private String productId;
        private String productName;
        private BigDecimal unitPrice;
        private String currency;
        private Integer quantity;
    }
}
