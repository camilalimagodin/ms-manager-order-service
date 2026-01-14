package io.github.douglasdreer.order.application.usecase;

import io.github.douglasdreer.order.application.dto.OrderResponse;
import io.github.douglasdreer.order.application.mapper.OrderApplicationMapper;
import io.github.douglasdreer.order.application.port.output.OrderRepositoryPort;
import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.entity.OrderItem;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetOrderUseCaseImpl")
class GetOrderUseCaseImplTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Spy
    private OrderApplicationMapper mapper = new OrderApplicationMapper();

    @InjectMocks
    private GetOrderUseCaseImpl useCase;

    private Order createTestOrder(UUID orderId) {
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
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("deve retornar pedido quando encontrado")
        void shouldReturnOrderWhenFound() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order testOrder = createTestOrder(orderId);
            when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(testOrder));

            // Act
            Optional<OrderResponse> result = useCase.findById(orderId);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(orderId);
            assertThat(result.get().getExternalOrderId()).isEqualTo("EXT-001");
            assertThat(result.get().getStatus()).isEqualTo("CALCULATED");
            
            verify(orderRepository).findByIdWithItems(orderId);
        }

        @Test
        @DisplayName("deve retornar empty quando não encontrado")
        void shouldReturnEmptyWhenNotFound() {
            // Arrange
            UUID unknownId = UUID.randomUUID();
            when(orderRepository.findByIdWithItems(unknownId)).thenReturn(Optional.empty());

            // Act
            Optional<OrderResponse> result = useCase.findById(unknownId);

            // Assert
            assertThat(result).isEmpty();
            verify(orderRepository).findByIdWithItems(unknownId);
        }
    }

    @Nested
    @DisplayName("findByExternalOrderId()")
    class FindByExternalOrderIdTests {

        @Test
        @DisplayName("deve retornar pedido quando encontrado por ID externo")
        void shouldReturnOrderWhenFoundByExternalId() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order testOrder = createTestOrder(orderId);
            when(orderRepository.findByExternalOrderId("EXT-001")).thenReturn(Optional.of(testOrder));

            // Act
            Optional<OrderResponse> result = useCase.findByExternalOrderId("EXT-001");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getExternalOrderId()).isEqualTo("EXT-001");
            
            verify(orderRepository).findByExternalOrderId("EXT-001");
        }

        @Test
        @DisplayName("deve retornar empty quando ID externo não encontrado")
        void shouldReturnEmptyWhenExternalIdNotFound() {
            // Arrange
            when(orderRepository.findByExternalOrderId("UNKNOWN")).thenReturn(Optional.empty());

            // Act
            Optional<OrderResponse> result = useCase.findByExternalOrderId("UNKNOWN");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByStatus()")
    class FindByStatusTests {

        @Test
        @DisplayName("deve retornar lista de pedidos por status")
        void shouldReturnOrdersByStatus() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order testOrder = createTestOrder(orderId);
            when(orderRepository.findByStatus(OrderStatus.CALCULATED)).thenReturn(List.of(testOrder));

            // Act
            List<OrderResponse> result = useCase.findByStatus("CALCULATED");

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo("CALCULATED");
            
            verify(orderRepository).findByStatus(OrderStatus.CALCULATED);
        }

        @Test
        @DisplayName("deve retornar lista vazia quando nenhum pedido com status")
        void shouldReturnEmptyListWhenNoOrdersWithStatus() {
            // Arrange
            when(orderRepository.findByStatus(OrderStatus.FAILED)).thenReturn(List.of());

            // Act
            List<OrderResponse> result = useCase.findByStatus("FAILED");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {

        @Test
        @DisplayName("deve retornar todos os pedidos")
        void shouldReturnAllOrders() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order testOrder = createTestOrder(orderId);
            when(orderRepository.findAll()).thenReturn(List.of(testOrder));

            // Act
            List<OrderResponse> result = useCase.findAll();

            // Assert
            assertThat(result).hasSize(1);
            verify(orderRepository).findAll();
        }
    }
}
