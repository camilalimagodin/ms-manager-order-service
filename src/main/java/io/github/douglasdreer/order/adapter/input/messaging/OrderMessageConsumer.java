package io.github.douglasdreer.order.adapter.input.messaging;

import io.github.douglasdreer.order.adapter.exception.MessageProcessingException;
import io.github.douglasdreer.order.adapter.output.messaging.event.OrderCreatedEvent;
import io.github.douglasdreer.order.application.port.input.CreateOrderUseCase;
import io.github.douglasdreer.order.application.dto.CreateOrderCommand;
import io.github.douglasdreer.order.domain.exception.DomainException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/** Consumer RabbitMQ para eventos de criação de pedidos (fila: order.created.queue). */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageConsumer {

    private final CreateOrderUseCase createOrderUseCase;
    private final Validator validator;

    /**
     * Consome mensagens OrderCreatedEvent do RabbitMQ.
     * Valida o evento e delega ao CreateOrderUseCase.
     *
     * @param event         Evento de criação de pedido do sistema externo
     * @param correlationId ID de correlação da mensagem para rastreabilidade
     */
    @RabbitListener(queues = "${rabbitmq.queues.order-created}")
    public void handleOrderCreatedEvent(
            @Payload OrderCreatedEvent event,
            @Header(AmqpHeaders.CORRELATION_ID) String correlationId) {

        log.info("OrderCreatedEvent recebido - correlationId: {}, customerId: {}, items: {}",
                correlationId, event.customerId(), event.items().size());

        try {
            // Valida o evento
            validateEvent(event);

            // Converte evento para comando
            CreateOrderCommand command = mapToCommand(event);

            // Processa criação do pedido
            var order = createOrderUseCase.execute(command);

            log.info("Pedido criado com sucesso - orderId: {}, correlationId: {}",
                    order.getId(), correlationId);

        } catch (DomainException e) {
            log.error("Erro de domínio processando OrderCreatedEvent - correlationId: {}, error: {}",
                    correlationId, e.getMessage(), e);
            throw e; // Será enviado para DLQ
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação processando OrderCreatedEvent - correlationId: {}, error: {}",
                    correlationId, e.getMessage(), e);
            throw e; // Será enviado para DLQ
        } catch (Exception e) {
            log.error("Erro inesperado processando OrderCreatedEvent - correlationId: {}",
                    correlationId, e);
            throw new MessageProcessingException("Falha ao processar evento de criação de pedido", e); // Será enviado para DLQ
        }
    }

    /**
     * Valida o evento usando Bean Validation
     */
    private void validateEvent(OrderCreatedEvent event) {
        Set<ConstraintViolation<OrderCreatedEvent>> violations = validator.validate(event);

        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> "%s: %s".formatted(v.getPropertyPath(), v.getMessage()))
                    .collect(Collectors.joining(", "));

            log.warn("OrderCreatedEvent inválido recebido: {}", errors);
            throw new IllegalArgumentException("Evento inválido: " + errors);
        }

        // Valida itens aninhados
        for (OrderCreatedEvent.OrderItemEvent item : event.items()) {
            Set<ConstraintViolation<OrderCreatedEvent.OrderItemEvent>> itemViolations =
                    validator.validate(item);

            if (!itemViolations.isEmpty()) {
                String errors = itemViolations.stream()
                        .map(v -> "%s: %s".formatted(v.getPropertyPath(), v.getMessage()))
                        .collect(Collectors.joining(", "));

                log.warn("OrderItemEvent inválido recebido: {}", errors);
                throw new IllegalArgumentException("Item inválido: " + errors);
            }
        }
    }

    /**
     * Mapeia OrderCreatedEvent para CreateOrderCommand
     */
    private CreateOrderCommand mapToCommand(OrderCreatedEvent event) {
        var items = event.items().stream()
                .map(item -> CreateOrderCommand.OrderItemCommand.builder()
                        .productId(item.productId())
                        .productName("") // Nome do produto não vem no evento
                        .unitPrice(item.price())
                        .currency("BRL")
                        .quantity(item.quantity())
                        .build()
                )
                .toList();

        return CreateOrderCommand.builder()
                .externalOrderId(event.customerId())
                .items(items)
                .build();
    }
}
