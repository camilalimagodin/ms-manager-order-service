package io.github.douglasdreer.order.application.port.input;

import io.github.douglasdreer.order.application.dto.OrderResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Porta de entrada para consulta de pedidos. */
public interface GetOrderUseCase {

    /** Busca pedido por ID interno. */
    Optional<OrderResponse> findById(UUID id);

    /** Busca pedido por ID externo. */
    Optional<OrderResponse> findByExternalOrderId(String externalOrderId);

    /** Busca pedidos por status. */
    List<OrderResponse> findByStatus(String status);

    /** Busca todos os pedidos. */
    List<OrderResponse> findAll();
}
