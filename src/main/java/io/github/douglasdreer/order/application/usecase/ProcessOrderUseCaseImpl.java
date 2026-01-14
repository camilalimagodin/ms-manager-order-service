package io.github.douglasdreer.order.application.usecase;

import io.github.douglasdreer.order.application.dto.OrderResponse;
import io.github.douglasdreer.order.application.mapper.OrderApplicationMapper;
import io.github.douglasdreer.order.application.port.input.ProcessOrderUseCase;
import io.github.douglasdreer.order.application.port.output.OrderRepositoryPort;
import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Caso de uso para processamento e mudanças de status de pedidos. */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProcessOrderUseCaseImpl implements ProcessOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderApplicationMapper mapper;

    @Override
    public OrderResponse process(UUID orderId) {
        log.info("Processando pedido: {}", orderId);

        Order order = findOrderOrThrow(orderId);

        // Iniciar processamento
        order.startProcessing();
        
        // Calcular totais
        order.calculateTotal();

        // Persistir
        Order savedOrder = orderRepository.save(order);

        log.info("Pedido processado: id={}, status={}, total={}", 
                savedOrder.getId(), 
                savedOrder.getStatus(),
                savedOrder.getTotalAmount());

        return mapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponse markAsAvailable(UUID orderId) {
        log.info("Marcando pedido como disponível: {}", orderId);

        Order order = findOrderOrThrow(orderId);

        // Marcar como disponível
        order.markAsAvailable();

        // Persistir
        Order savedOrder = orderRepository.save(order);

        log.info("Pedido disponível: id={}, status={}", 
                savedOrder.getId(), 
                savedOrder.getStatus());

        return mapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponse markAsFailed(UUID orderId, String reason) {
        log.warn("Marcando pedido como falha: id={}, motivo={}", orderId, reason);

        Order order = findOrderOrThrow(orderId);

        // Marcar como falha
        order.markAsFailed();

        // Persistir
        Order savedOrder = orderRepository.save(order);

        log.warn("Pedido falhou: id={}, status={}", 
                savedOrder.getId(), 
                savedOrder.getStatus());

        return mapper.toResponse(savedOrder);
    }

    private Order findOrderOrThrow(UUID orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> OrderNotFoundException.byId(orderId.toString()));
    }
}
