package io.github.douglasdreer.order.application.usecase;

import io.github.douglasdreer.order.application.dto.CreateOrderCommand;
import io.github.douglasdreer.order.application.dto.OrderResponse;
import io.github.douglasdreer.order.application.mapper.OrderApplicationMapper;
import io.github.douglasdreer.order.application.port.input.CreateOrderUseCase;
import io.github.douglasdreer.order.application.port.output.OrderRepositoryPort;
import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.exception.DuplicateOrderException;
import io.github.douglasdreer.order.domain.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação do caso de uso de criação de pedido.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderApplicationMapper mapper;

    @Override
    public OrderResponse execute(CreateOrderCommand command) {
        // Validações primeiro
        validateCommand(command);
        
        log.info("Criando pedido: externalOrderId={}", command.getExternalOrderId());
        
        checkDuplicateOrder(command.getExternalOrderId());

        // Converter para domínio (já inclui cálculo de totais via builder)
        Order order = mapper.toDomain(command);

        // Calcular totais
        order.calculateTotal();

        // Persistir
        Order savedOrder = orderRepository.save(order);

        log.info("Pedido criado com sucesso: id={}, externalOrderId={}, total={}", 
                savedOrder.getId(), 
                savedOrder.getExternalOrderIdValue(),
                savedOrder.getTotalAmount());

        return mapper.toResponse(savedOrder);
    }

    private void validateCommand(CreateOrderCommand command) {
        if (command == null) {
            throw new ValidationException("Comando de criação não pode ser nulo");
        }
        if (command.getExternalOrderId() == null || command.getExternalOrderId().isBlank()) {
            throw new ValidationException("ID externo do pedido é obrigatório");
        }
        if (command.getItems() == null || command.getItems().isEmpty()) {
            throw new ValidationException("Pedido deve ter pelo menos um item");
        }

        for (CreateOrderCommand.OrderItemCommand item : command.getItems()) {
            validateItem(item);
        }
    }

    private void validateItem(CreateOrderCommand.OrderItemCommand item) {
        if (item.getProductId() == null || item.getProductId().isBlank()) {
            throw new ValidationException("ID do produto é obrigatório");
        }
        if (item.getProductName() == null || item.getProductName().isBlank()) {
            throw new ValidationException("Nome do produto é obrigatório");
        }
        if (item.getUnitPrice() == null || item.getUnitPrice().signum() <= 0) {
            throw new ValidationException("Preço unitário deve ser maior que zero");
        }
        if (item.getQuantity() == null || item.getQuantity() <= 0) {
            throw new ValidationException("Quantidade deve ser maior que zero");
        }
    }

    private void checkDuplicateOrder(String externalOrderId) {
        if (orderRepository.existsByExternalOrderId(externalOrderId)) {
            throw new DuplicateOrderException(externalOrderId);
        }
    }
}
