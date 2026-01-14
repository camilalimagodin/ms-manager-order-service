package io.github.douglasdreer.order.adapter.input.messaging;

import io.github.douglasdreer.order.adapter.exception.MessageProcessingException;
import io.github.douglasdreer.order.adapter.output.messaging.event.OrderCreatedEvent;
import io.github.douglasdreer.order.application.port.input.CreateOrderUseCase;
import io.github.douglasdreer.order.application.dto.CreateOrderCommand;
import io.github.douglasdreer.order.application.dto.OrderResponse;
import io.github.douglasdreer.order.domain.exception.ValidationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderMessageConsumer Tests")
class OrderMessageConsumerTest {

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private Validator validator;

    @InjectMocks
    private OrderMessageConsumer consumer;

    private OrderCreatedEvent validEvent;
    private String correlationId;

    @BeforeEach
    void setUp() {
        correlationId = UUID.randomUUID().toString();

        var item = OrderCreatedEvent.OrderItemEvent.builder()
                .productId("PROD-001")
                .quantity(2)
                .price(new BigDecimal("99.99"))
                .build();

        validEvent = OrderCreatedEvent.builder()
                .correlationId(correlationId)
                .customerId("CUST-123")
                .items(List.of(item))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve processar OrderCreatedEvent válido com sucesso")
    void shouldProcessValidEvent() {
        // Dado
        when(validator.validate(any(OrderCreatedEvent.class))).thenReturn(Collections.emptySet());
        when(validator.validate(any(OrderCreatedEvent.OrderItemEvent.class))).thenReturn(Collections.emptySet());

        var orderResponse = OrderResponse.builder()
                .id(UUID.randomUUID())
                .externalOrderId("CUST-123")
                .status("PENDING")
                .build();

        when(createOrderUseCase.execute(any(CreateOrderCommand.class))).thenReturn(orderResponse);

        // Quando
        assertThatCode(() -> consumer.handleOrderCreatedEvent(validEvent, correlationId))
                .doesNotThrowAnyException();

        // Então
        verify(validator).validate(validEvent);
        verify(createOrderUseCase).execute(any(CreateOrderCommand.class));

        ArgumentCaptor<CreateOrderCommand> commandCaptor = ArgumentCaptor.forClass(CreateOrderCommand.class);
        verify(createOrderUseCase).execute(commandCaptor.capture());

        CreateOrderCommand capturedCommand = commandCaptor.getValue();
        assertThat(capturedCommand.getExternalOrderId()).isEqualTo("CUST-123");
        assertThat(capturedCommand.getItems()).hasSize(1);
        assertThat(capturedCommand.getItems().get(0).getProductId()).isEqualTo("PROD-001");
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação do evento falha")
    void shouldThrowExceptionWhenEventValidationFails() {
        // Dado
        when(validator.validate(any(OrderCreatedEvent.class)))
                .thenReturn(Set.of(mock(jakarta.validation.ConstraintViolation.class)));

        // Quando/Then
        assertThatThrownBy(() -> consumer.handleOrderCreatedEvent(validEvent, correlationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Evento inválido");

        verify(createOrderUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando evento não tem itens")
    void shouldThrowExceptionWhenEventHasNoItems() {
        // Dado
        var eventWithNoItems = OrderCreatedEvent.builder()
                .correlationId(correlationId)
                .customerId("CUST-123")
                .items(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .build();

        when(validator.validate(any(OrderCreatedEvent.class)))
                .thenReturn(Set.of(mock(jakarta.validation.ConstraintViolation.class)));

        // Quando/Then
        assertThatThrownBy(() -> consumer.handleOrderCreatedEvent(eventWithNoItems, correlationId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(createOrderUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve propagar DomainException do caso de uso")
    void shouldPropagateDomainException() {
        // Dado
        when(validator.validate(any(OrderCreatedEvent.class))).thenReturn(Collections.emptySet());
        when(validator.validate(any(OrderCreatedEvent.OrderItemEvent.class))).thenReturn(Collections.emptySet());

        var domainException = new ValidationException("Invalid order data");
        when(createOrderUseCase.execute(any())).thenThrow(domainException);

        // Quando/Then
        assertThatThrownBy(() -> consumer.handleOrderCreatedEvent(validEvent, correlationId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid order data");
    }

    @Test
    @DisplayName("Deve encapsular exceções inesperadas em MessageProcessingException")
    void shouldWrapUnexpectedExceptions() {
        // Dado
        when(validator.validate(any(OrderCreatedEvent.class))).thenReturn(Collections.emptySet());
        when(validator.validate(any(OrderCreatedEvent.OrderItemEvent.class))).thenReturn(Collections.emptySet());

        when(createOrderUseCase.execute(any())).thenThrow(new NullPointerException("Unexpected error"));

        // Quando/Then
        assertThatThrownBy(() -> consumer.handleOrderCreatedEvent(validEvent, correlationId))
                .isInstanceOf(MessageProcessingException.class)
                .hasMessageContaining("Falha ao processar evento de criação de pedido");
    }

    @Test
    @DisplayName("Deve validar todos os itens no evento")
    void shouldValidateAllItems() {
        // Dado
        var item1 = OrderCreatedEvent.OrderItemEvent.builder()
                .productId("PROD-001")
                .quantity(2)
                .price(new BigDecimal("99.99"))
                .build();

        var item2 = OrderCreatedEvent.OrderItemEvent.builder()
                .productId("PROD-002")
                .quantity(1)
                .price(new BigDecimal("49.99"))
                .build();

        var eventWithMultipleItems = OrderCreatedEvent.builder()
                .correlationId(correlationId)
                .customerId("CUST-123")
                .items(List.of(item1, item2))
                .createdAt(LocalDateTime.now())
                .build();

        when(validator.validate(any(OrderCreatedEvent.class))).thenReturn(Collections.emptySet());
        when(validator.validate(any(OrderCreatedEvent.OrderItemEvent.class))).thenReturn(Collections.emptySet());

        var orderResponse = OrderResponse.builder()
                .id(UUID.randomUUID())
                .externalOrderId("CUST-123")
                .status("PENDING")
                .build();

        when(createOrderUseCase.execute(any())).thenReturn(orderResponse);

        // Quando
        consumer.handleOrderCreatedEvent(eventWithMultipleItems, correlationId);

        // Então
        verify(validator, times(2)).validate(any(OrderCreatedEvent.OrderItemEvent.class));
    }

    @Test
    @DisplayName("Deve mapear evento para comando corretamente")
    void shouldMapEventToCommand() {
        // Dado
        when(validator.validate(any())).thenReturn(Collections.emptySet());

        var orderResponse = OrderResponse.builder()
                .id(UUID.randomUUID())
                .externalOrderId("CUST-123")
                .status("PENDING")
                .build();

        when(createOrderUseCase.execute(any())).thenReturn(orderResponse);

        // Quando
        consumer.handleOrderCreatedEvent(validEvent, correlationId);

        // Então
        ArgumentCaptor<CreateOrderCommand> commandCaptor = ArgumentCaptor.forClass(CreateOrderCommand.class);
        verify(createOrderUseCase).execute(commandCaptor.capture());

        CreateOrderCommand command = commandCaptor.getValue();
        assertThat(command.getExternalOrderId()).isEqualTo("CUST-123");
        assertThat(command.getItems()).hasSize(1);

        var commandItem = command.getItems().get(0);
        var eventItem = validEvent.items().get(0);
        assertThat(commandItem.getProductId()).isEqualTo(eventItem.productId());
        assertThat(commandItem.getQuantity()).isEqualTo(eventItem.quantity());
        assertThat(commandItem.getUnitPrice()).isEqualTo(eventItem.price());
    }
}
