package io.github.douglasdreer.order.application.port.input;

import io.github.douglasdreer.order.application.dto.OrderResponse;

import java.util.UUID;

/** Porta de entrada para processamento de pedidos. */
public interface ProcessOrderUseCase {

    /** Processa um pedido (calcula totais e atualiza status). */
    OrderResponse process(UUID orderId);

    /** Marca pedido como disponível para consulta externa. */
    OrderResponse markAsAvailable(UUID orderId);

    /** Marca pedido como falha. */
    OrderResponse markAsFailed(UUID orderId, String reason);
}
