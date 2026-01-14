package io.github.douglasdreer.order.application.usecase;

import io.github.douglasdreer.order.application.dto.OrderResponse;
import io.github.douglasdreer.order.application.mapper.OrderApplicationMapper;
import io.github.douglasdreer.order.application.port.input.GetOrderUseCase;
import io.github.douglasdreer.order.application.port.output.OrderRepositoryPort;
import io.github.douglasdreer.order.domain.valueobject.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do caso de uso de consulta de pedidos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetOrderUseCaseImpl implements GetOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderApplicationMapper mapper;

    @Override
    public Optional<OrderResponse> findById(UUID id) {
        log.debug("Buscando pedido por ID: {}", id);
        
        return orderRepository.findByIdWithItems(id)
                .map(mapper::toResponse);
    }

    @Override
    public Optional<OrderResponse> findByExternalOrderId(String externalOrderId) {
        log.debug("Buscando pedido por ID externo: {}", externalOrderId);
        
        return orderRepository.findByExternalOrderId(externalOrderId)
                .map(mapper::toResponse);
    }

    @Override
    public List<OrderResponse> findByStatus(String status) {
        log.debug("Buscando pedidos por status: {}", status);
        
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        return mapper.toResponseList(orderRepository.findByStatus(orderStatus));
    }

    @Override
    public List<OrderResponse> findAll() {
        log.debug("Buscando todos os pedidos");
        
        return mapper.toResponseList(orderRepository.findAll());
    }
}
