package io.github.douglasdreer.order.application.usecase;

import io.github.douglasdreer.order.application.dto.OrderResponse;
import io.github.douglasdreer.order.application.mapper.OrderApplicationMapper;
import io.github.douglasdreer.order.application.port.output.OrderRepositoryPort;
import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.entity.OrderItem;
import io.github.douglasdreer.order.domain.exception.OrderNotFoundException;
import io.github.douglasdreer.order.domain.valueobject.Money;
import io.github.douglasdreer.order.domain.valueobject.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcessOrderUseCaseImpl")
class ProcessOrderUseCaseImplTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Spy
    private OrderApplicationMapper mapper = new OrderApplicationMapper();

    @InjectMocks
    private ProcessOrderUseCaseImpl useCase;

    private Order createReceivedOrder(UUID orderId) {
        OrderItem item = OrderItem.builder()
                .productId("PROD-001")
                .productName("Produto Teste")
                .unitPrice(Money.of(new BigDecimal("100.00")))
                .quantity(2)
                .build();

        return Order.builder()
                .id(orderId)
                .externalOrderId("EXT-001")
                .items(List.of(item))
                .status(OrderStatus.RECEIVED)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private Order createCalculatedOrder(UUID orderId) {
        OrderItem item = OrderItem.builder()
                .productId("PROD-001")
                .productName("Produto Teste")
                .unitPrice(Money.of(new BigDecimal("100.00")))
                .quantity(2)
                .build();

        return Order.builder()
                .id(orderId)
                .externalOrderId("EXT-001")
                .items(List.of(item))
                .status(OrderStatus.CALCULATED)
                .totalAmount(Money.of(new BigDecimal("200.00")))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("process()")
    class ProcessTests {

        @Test
        @DisplayName("deve processar pedido com sucesso")
        void shouldProcessOrderSuccessfully() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order receivedOrder = createReceivedOrder(orderId);
            when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(receivedOrder));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            OrderResponse result = useCase.process(orderId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(orderId);
            assertThat(result.getStatus()).isEqualTo(OrderStatus.CALCULATED.name());
            assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("200.00"));
            
            verify(orderRepository).findByIdWithItems(orderId);
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("deve lançar exceção quando pedido não encontrado")
        void shouldThrowExceptionWhenOrderNotFound() {
            // Arrange
            UUID unknownId = UUID.randomUUID();
            when(orderRepository.findByIdWithItems(unknownId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> useCase.process(unknownId))
                    .isInstanceOf(OrderNotFoundException.class);
            
            verify(orderRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("markAsAvailable()")
    class MarkAsAvailableTests {

        @Test
        @DisplayName("deve marcar pedido como disponível")
        void shouldMarkOrderAsAvailable() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order calculatedOrder = createCalculatedOrder(orderId);
            when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(calculatedOrder));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            OrderResponse result = useCase.markAsAvailable(orderId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.AVAILABLE.name());
            
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("deve lançar exceção quando pedido não encontrado")
        void shouldThrowExceptionWhenOrderNotFound() {
            // Arrange
            UUID unknownId = UUID.randomUUID();
            when(orderRepository.findByIdWithItems(unknownId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> useCase.markAsAvailable(unknownId))
                    .isInstanceOf(OrderNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("markAsFailed()")
    class MarkAsFailedTests {

        @Test
        @DisplayName("deve marcar pedido como falha")
        void shouldMarkOrderAsFailed() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order receivedOrder = createReceivedOrder(orderId);
            when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(receivedOrder));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            OrderResponse result = useCase.markAsFailed(orderId, "Erro de processamento");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.FAILED.name());
            
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("deve lançar exceção quando pedido não encontrado")
        void shouldThrowExceptionWhenOrderNotFound() {
            // Arrange
            UUID unknownId = UUID.randomUUID();
            when(orderRepository.findByIdWithItems(unknownId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> useCase.markAsFailed(unknownId, "Erro"))
                    .isInstanceOf(OrderNotFoundException.class);
        }
    }
}
