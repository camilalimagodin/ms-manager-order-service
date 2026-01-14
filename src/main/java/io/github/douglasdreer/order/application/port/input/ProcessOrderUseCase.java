package io.github.douglasdreer.order.application.port.input;

import io.github.douglasdreer.order.application.dto.OrderResponse;

import java.util.UUID;

/**
 * Porta de entrada para processamento de pedidos.
 */
public interface ProcessOrderUseCase {

    /**
     * Processa um pedido (calcula totais e atualiza status).
     *
     * @param orderId identificador do pedido
     * @return pedido processado
     */
    OrderResponse process(UUID orderId);

    /**
     * Marca pedido como disponível para consulta externa.
     *
     * @param orderId identificador do pedido
     * @return pedido atualizado
     */
    OrderResponse markAsAvailable(UUID orderId);

    /**
     * Marca pedido como falha.
     *
     * @param orderId identificador do pedido
     * @param reason motivo da falha
     * @return pedido atualizado
     */
    OrderResponse markAsFailed(UUID orderId, String reason);
}
