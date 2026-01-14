package io.github.douglasdreer.order.application.port.input;

import io.github.douglasdreer.order.application.dto.CreateOrderCommand;
import io.github.douglasdreer.order.application.dto.OrderResponse;

/**
 * Porta de entrada para criação de pedidos.
 */
public interface CreateOrderUseCase {

    /**
     * Cria um novo pedido.
     *
     * @param command dados do pedido a ser criado
     * @return pedido criado
     */
    OrderResponse execute(CreateOrderCommand command);
}
