package io.github.douglasdreer.order.application.port.input;

import io.github.douglasdreer.order.application.dto.OrderResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de entrada para consulta de pedidos.
 */
public interface GetOrderUseCase {

    /**
     * Busca pedido por ID interno.
     *
     * @param id identificador único
     * @return pedido encontrado ou empty
     */
    Optional<OrderResponse> findById(UUID id);

    /**
     * Busca pedido por ID externo.
     *
     * @param externalOrderId identificador externo
     * @return pedido encontrado ou empty
     */
    Optional<OrderResponse> findByExternalOrderId(String externalOrderId);

    /**
     * Busca pedidos por status.
     *
     * @param status status do pedido
     * @return lista de pedidos
     */
    List<OrderResponse> findByStatus(String status);

    /**
     * Busca todos os pedidos.
     *
     * @return lista de pedidos
     */
    List<OrderResponse> findAll();
}
