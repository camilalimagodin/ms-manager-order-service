package io.github.douglasdreer.order.adapter.output.messaging;

import io.github.douglasdreer.order.adapter.exception.MessagePublishingException;
import io.github.douglasdreer.order.adapter.output.messaging.event.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publisher para eventos relacionados a pedidos no RabbitMQ.
 * Publica mudanças de status de pedidos para sistemas externos (Produto Externo B).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchanges.order}")
    private String orderExchange;

    @Value("${rabbitmq.routing-keys.status-changed}")
    private String statusChangedRoutingKey;

    /**
     * Publica um OrderStatusChangedEvent no RabbitMQ.
     *
     * @param event Evento de mudança de status a ser publicado
     * @throws MessagePublishingException se o evento não puder ser publicado
     */
    public void publishOrderStatusChanged(OrderStatusChangedEvent event) {
        try {
            log.info("Publicando OrderStatusChangedEvent - orderId: {}, status: {} -> {}, correlationId: {}",
                    event.orderId(), event.previousStatus(), event.currentStatus(), event.correlationId());

            rabbitTemplate.convertAndSend(
                    orderExchange,
                    statusChangedRoutingKey,
                    event,
                    message -> {
                        // Adiciona correlation ID às propriedades da mensagem se presente
                        if (event.correlationId() != null) {
                            message.getMessageProperties().setCorrelationId(event.correlationId());
                        }
                        return message;
                    }
            );

            log.info("OrderStatusChangedEvent publicado com sucesso - orderId: {}", event.orderId());

        } catch (Exception e) {
            log.error("Falha ao publicar OrderStatusChangedEvent - orderId: {}, correlationId: {}",
                    event.orderId(), event.correlationId(), e);
            throw new MessagePublishingException("Falha ao publicar evento de mudança de status de pedido", e);
        }
    }
}
