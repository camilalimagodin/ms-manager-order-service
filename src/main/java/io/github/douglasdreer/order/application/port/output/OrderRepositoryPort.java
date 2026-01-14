package io.github.douglasdreer.order.application.port.output;

import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.valueobject.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saída para persistência de pedidos.
 * Define o contrato que a camada de infraestrutura deve implementar.
 */
public interface OrderRepositoryPort {

    /**
     * Salva um pedido.
     *
     * @param order pedido a ser salvo
     * @return pedido salvo com ID gerado
     */
    Order save(Order order);

    /**
     * Busca pedido por ID interno.
     *
     * @param id identificador único
     * @return pedido encontrado ou empty
     */
    Optional<Order> findById(UUID id);

    /**
     * Busca pedido por ID externo.
     *
     * @param externalOrderId identificador externo
     * @return pedido encontrado ou empty
     */
    Optional<Order> findByExternalOrderId(String externalOrderId);

    /**
     * Verifica se existe pedido com o ID externo.
     *
     * @param externalOrderId identificador externo
     * @return true se existir
     */
    boolean existsByExternalOrderId(String externalOrderId);

    /**
     * Busca pedidos por status.
     *
     * @param status status do pedido
     * @return lista de pedidos
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Busca pedidos criados em um período.
     *
     * @param startDate data inicial
     * @param endDate data final
     * @return lista de pedidos
     */
    List<Order> findByCreatedAtBetween(Instant startDate, Instant endDate);

    /**
     * Busca todos os pedidos.
     *
     * @return lista de todos os pedidos
     */
    List<Order> findAll();

    /**
     * Remove um pedido por ID.
     *
     * @param id identificador único
     */
    void deleteById(UUID id);

    /**
     * Conta pedidos por status.
     *
     * @param status status do pedido
     * @return quantidade de pedidos
     */
    long countByStatus(OrderStatus status);

    /**
     * Busca pedido por ID com itens carregados.
     *
     * @param id identificador único
     * @return pedido com itens ou empty
     */
    Optional<Order> findByIdWithItems(UUID id);
}
