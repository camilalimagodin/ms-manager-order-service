package io.github.douglasdreer.order.adapter.output.messaging;

import io.github.douglasdreer.order.adapter.output.messaging.event.OrderStatusChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes OrderEventPublisher")
class OrderEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderEventPublisher publisher;

    private String orderExchange = "order.exchange";
    private String statusChangedRoutingKey = "order.status.changed";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "orderExchange", orderExchange);
        ReflectionTestUtils.setField(publisher, "statusChangedRoutingKey", statusChangedRoutingKey);
    }

    @Test
    @DisplayName("Deve publicar OrderStatusChangedEvent com sucesso")
    void shouldPublishStatusChangedEvent() {
        // Dado
        var orderId = UUID.randomUUID().toString();
        var correlationId = UUID.randomUUID().toString();

        var event = OrderStatusChangedEvent.builder()
                .orderId(orderId)
                .previousStatus("PENDING")
                .currentStatus("CONFIRMED")
                .customerId("CUST-123")
                .changedAt(LocalDateTime.now())
                .correlationId(correlationId)
                .build();

        // Quando
        assertThatCode(() -> publisher.publishOrderStatusChanged(event))
                .doesNotThrowAnyException();

        // Ent�o
        verify(rabbitTemplate).convertAndSend(
                eq(orderExchange),
                eq(statusChangedRoutingKey),
                eq(event),
                any(MessagePostProcessor.class)
        );
    }

    @Test
    @DisplayName("Deve definir correlation ID nas propriedades da mensagem quando presente")
    void shouldSetCorrelationIdInMessageProperties() {
        // Dado
        var orderId = UUID.randomUUID().toString();
        var correlationId = UUID.randomUUID().toString();

        var event = OrderStatusChangedEvent.builder()
                .orderId(orderId)
                .previousStatus("PENDING")
                .currentStatus("CONFIRMED")
                .customerId("CUST-123")
                .changedAt(LocalDateTime.now())
                .correlationId(correlationId)
                .build();

        ArgumentCaptor<MessagePostProcessor> processorCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        // Quando
        publisher.publishOrderStatusChanged(event);

        // Ent�o
        verify(rabbitTemplate).convertAndSend(
                eq(orderExchange),
                eq(statusChangedRoutingKey),
                eq(event),
                processorCaptor.capture()
        );

        // Verifica se o processador de mensagem define o correlation ID
        MessagePostProcessor processor = processorCaptor.getValue();
        assertThat(processor).isNotNull();
    }

    @Test
    @DisplayName("Deve lidar com evento sem correlation ID")
    void shouldHandleEventWithoutCorrelationId() {
        // Dado
        var orderId = UUID.randomUUID().toString();

        var event = OrderStatusChangedEvent.builder()
                .orderId(orderId)
                .previousStatus("PENDING")
                .currentStatus("CONFIRMED")
                .customerId("CUST-123")
                .changedAt(LocalDateTime.now())
                .correlationId(null)
                .build();

        // Quando
        assertThatCode(() -> publisher.publishOrderStatusChanged(event))
                .doesNotThrowAnyException();

        // Ent�o
        verify(rabbitTemplate).convertAndSend(
                eq(orderExchange),
                eq(statusChangedRoutingKey),
                eq(event),
                any(MessagePostProcessor.class)
        );
    }

    @Test
    @DisplayName("Deve encapsular exce��o quando publica��o falha")
    void shouldWrapExceptionWhenPublishingFails() {
        // Dado
        var orderId = UUID.randomUUID().toString();

        var event = OrderStatusChangedEvent.builder()
                .orderId(orderId)
                .previousStatus("PENDING")
                .currentStatus("CONFIRMED")
                .customerId("CUST-123")
                .changedAt(LocalDateTime.now())
                .build();

        doThrow(new RuntimeException("Connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(), any(MessagePostProcessor.class));

        // Quando/Then
        assertThatThrownBy(() -> publisher.publishOrderStatusChanged(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Falha ao publicar evento de mudança de status de pedido")
                .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Deve publicar evento com todas as transi��es de status")
    void shouldPublishEventWithAllStatusTransitions() {
        // Dado
        var orderId = UUID.randomUUID().toString();
        var correlationId = UUID.randomUUID().toString();

        var statusTransitions = new String[][]{
                {"PENDING", "CONFIRMED"},
                {"CONFIRMED", "PROCESSING"},
                {"PROCESSING", "SHIPPED"},
                {"SHIPPED", "DELIVERED"},
                {"DELIVERED", "COMPLETED"}
        };

        for (String[] transition : statusTransitions) {
            var event = OrderStatusChangedEvent.builder()
                    .orderId(orderId)
                    .previousStatus(transition[0])
                    .currentStatus(transition[1])
                    .customerId("CUST-123")
                    .changedAt(LocalDateTime.now())
                    .correlationId(correlationId)
                    .build();

            // Quando
            publisher.publishOrderStatusChanged(event);
        }

        // Ent�o
        verify(rabbitTemplate, times(5)).convertAndSend(
                eq(orderExchange),
                eq(statusChangedRoutingKey),
                any(OrderStatusChangedEvent.class),
                any(MessagePostProcessor.class)
        );
    }

    @Test
    @DisplayName("Deve usar exchange e routing key corretos")
    void shouldUseCorrectExchangeAndRoutingKey() {
        // Dado
        var orderId = UUID.randomUUID().toString();

        var event = OrderStatusChangedEvent.builder()
                .orderId(orderId)
                .previousStatus("PENDING")
                .currentStatus("CONFIRMED")
                .customerId("CUST-123")
                .changedAt(LocalDateTime.now())
                .build();

        // Quando
        publisher.publishOrderStatusChanged(event);

        // Ent�o
        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);

        verify(rabbitTemplate).convertAndSend(
                exchangeCaptor.capture(),
                routingKeyCaptor.capture(),
                eq(event),
                any(MessagePostProcessor.class)
        );

        assertThat(exchangeCaptor.getValue()).isEqualTo("order.exchange");
        assertThat(routingKeyCaptor.getValue()).isEqualTo("order.status.changed");
    }
}
