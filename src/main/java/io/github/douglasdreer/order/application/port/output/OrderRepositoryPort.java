package io.github.douglasdreer.order.application.port.output;

import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.valueobject.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Porta de saída para persistência de pedidos. */
public interface OrderRepositoryPort {

    /** Salva um pedido. */
    Order save(Order order);

    /** Busca pedido por ID interno. */
    Optional<Order> findById(UUID id);

    /** Busca pedido por ID externo. */
    Optional<Order> findByExternalOrderId(String externalOrderId);

    /** Verifica se existe pedido com o ID externo. */
    boolean existsByExternalOrderId(String externalOrderId);

    /** Busca pedidos por status. */
    List<Order> findByStatus(OrderStatus status);

    /** Busca pedidos criados em um período. */
    List<Order> findByCreatedAtBetween(Instant startDate, Instant endDate);

    /** Busca todos os pedidos. */
    List<Order> findAll();

    /** Remove um pedido por ID. */
    void deleteById(UUID id);

    /** Conta pedidos por status. */
    long countByStatus(OrderStatus status);

    /** Busca pedido por ID com itens carregados. */
    Optional<Order> findByIdWithItems(UUID id);
}
