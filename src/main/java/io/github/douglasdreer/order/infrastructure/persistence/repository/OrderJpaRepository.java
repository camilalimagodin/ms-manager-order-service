package io.github.douglasdreer.order.infrastructure.persistence.repository;

import io.github.douglasdreer.order.infrastructure.persistence.entity.OrderEntity;
import io.github.douglasdreer.order.infrastructure.persistence.entity.OrderStatusEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para OrderEntity.
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    /**
     * Busca pedido pelo ID externo.
     */
    Optional<OrderEntity> findByExternalOrderId(String externalOrderId);

    /**
     * Verifica se existe pedido com o ID externo.
     */
    boolean existsByExternalOrderId(String externalOrderId);

    /**
     * Busca pedidos por status.
     */
    List<OrderEntity> findByStatus(OrderStatusEntity status);

    /**
     * Busca pedidos por status com paginação.
     */
    Page<OrderEntity> findByStatus(OrderStatusEntity status, Pageable pageable);

    /**
     * Busca pedidos criados em um período.
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<OrderEntity> findByCreatedAtBetween(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    /**
     * Busca pedidos por status criados após uma data.
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.status = :status AND o.createdAt >= :since ORDER BY o.createdAt DESC")
    List<OrderEntity> findByStatusAndCreatedAtAfter(
            @Param("status") OrderStatusEntity status,
            @Param("since") Instant since
    );

    /**
     * Conta pedidos por status.
     */
    long countByStatus(OrderStatusEntity status);

    /**
     * Busca pedidos com itens (fetch join para evitar N+1).
     */
    @Query("SELECT DISTINCT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithItems(@Param("id") UUID id);

    /**
     * Busca pedidos disponíveis com itens.
     */
    @Query("SELECT DISTINCT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.status = 'AVAILABLE'")
    List<OrderEntity> findAvailableOrdersWithItems();
}
