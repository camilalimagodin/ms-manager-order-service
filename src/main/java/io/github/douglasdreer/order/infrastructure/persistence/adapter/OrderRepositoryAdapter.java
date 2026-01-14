package io.github.douglasdreer.order.infrastructure.persistence.adapter;

import io.github.douglasdreer.order.application.port.output.OrderRepositoryPort;
import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.valueobject.OrderStatus;
import io.github.douglasdreer.order.infrastructure.persistence.entity.OrderEntity;
import io.github.douglasdreer.order.infrastructure.persistence.mapper.OrderPersistenceMapper;
import io.github.douglasdreer.order.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do repositório de pedidos.
 * Adapter que conecta a porta de saída com o repositório JPA.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository jpaRepository;
    private final OrderPersistenceMapper mapper;

    @Override
    @Transactional
    public Order save(Order order) {
        log.debug("Salvando pedido: {}", order.getExternalOrderIdValue());
        
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = jpaRepository.save(entity);
        
        log.info("Pedido salvo com sucesso: id={}, externalId={}", 
                savedEntity.getId(), savedEntity.getExternalOrderId());
        
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        log.debug("Buscando pedido por ID: {}", id);
        
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Order> findByExternalOrderId(String externalOrderId) {
        log.debug("Buscando pedido por ID externo: {}", externalOrderId);
        
        return jpaRepository.findByExternalOrderId(externalOrderId)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByExternalOrderId(String externalOrderId) {
        return jpaRepository.existsByExternalOrderId(externalOrderId);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        log.debug("Buscando pedidos por status: {}", status);
        
        return mapper.toDomainList(
                jpaRepository.findByStatus(mapper.toStatusEntity(status))
        );
    }

    @Override
    public List<Order> findByCreatedAtBetween(Instant startDate, Instant endDate) {
        log.debug("Buscando pedidos entre {} e {}", startDate, endDate);
        
        return mapper.toDomainList(
                jpaRepository.findByCreatedAtBetween(startDate, endDate)
        );
    }

    @Override
    public List<Order> findAll() {
        log.debug("Buscando todos os pedidos");
        
        return mapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.debug("Removendo pedido: {}", id);
        
        jpaRepository.deleteById(id);
        
        log.info("Pedido removido: {}", id);
    }

    @Override
    public long countByStatus(OrderStatus status) {
        return jpaRepository.countByStatus(mapper.toStatusEntity(status));
    }

    @Override
    public Optional<Order> findByIdWithItems(UUID id) {
        log.debug("Buscando pedido com itens: {}", id);
        
        return jpaRepository.findByIdWithItems(id)
                .map(mapper::toDomain);
    }
}
