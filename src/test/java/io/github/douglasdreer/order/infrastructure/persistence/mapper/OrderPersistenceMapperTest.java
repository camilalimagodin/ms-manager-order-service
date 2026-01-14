package io.github.douglasdreer.order.infrastructure.persistence.mapper;

import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.entity.OrderItem;
import io.github.douglasdreer.order.domain.valueobject.*;
import io.github.douglasdreer.order.infrastructure.persistence.entity.OrderEntity;
import io.github.douglasdreer.order.infrastructure.persistence.entity.OrderItemEntity;
import io.github.douglasdreer.order.infrastructure.persistence.entity.OrderStatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderPersistenceMapper - Testes de mapeamento JPA")
class OrderPersistenceMapperTest {

    private OrderPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderPersistenceMapper();
    }

    @Nested
    @DisplayName("Testes de toEntity")
    class ToEntityTests {

        @Test
        @DisplayName("Deve converter Order para OrderEntity")
        void shouldConvertOrderToEntity() {
            // Arrange
            OrderItem item = OrderItem.builder()
                    .id(UUID.randomUUID())
                    .productId(ProductId.of("PROD-001"))
                    .productName("Produto Teste")
                    .unitPrice(Money.of(BigDecimal.valueOf(100), Currency.getInstance("BRL")))
                    .quantity(2)
                    .createdAt(Instant.now())
                    .build();

            Order order = Order.builder()
                    .id(UUID.randomUUID())
                    .externalOrderId(ExternalOrderId.of("EXT-001"))
                    .items(List.of(item))
                    .totalAmount(Money.of(BigDecimal.valueOf(200), Currency.getInstance("BRL")))
                    .status(OrderStatus.RECEIVED)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .version(0L)
                    .build();

            // Act
            OrderEntity result = mapper.toEntity(order);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(order.getId());
            assertThat(result.getExternalOrderId()).isEqualTo("EXT-001");
            assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(200));
            assertThat(result.getTotalCurrency()).isEqualTo("BRL");
            assertThat(result.getStatus()).isEqualTo(OrderStatusEntity.RECEIVED);
            assertThat(result.getItems()).hasSize(1);
        }

        @Test
        @DisplayName("Deve retornar null quando Order é null")
        void shouldReturnNullWhenOrderIsNull() {
            // Act
            OrderEntity result = mapper.toEntity(null);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Testes de toDomain")
    class ToDomainTests {

        @Test
        @DisplayName("Deve converter OrderEntity para Order")
        void shouldConvertEntityToOrder() {
            // Arrange
            OrderItemEntity itemEntity = OrderItemEntity.builder()
                    .id(UUID.randomUUID())
                    .productId("PROD-001")
                    .productName("Produto Teste")
                    .unitPrice(BigDecimal.valueOf(100))
                    .unitCurrency("BRL")
                    .quantity(2)
                    .subtotal(BigDecimal.valueOf(200))
                    .subtotalCurrency("BRL")
                    .createdAt(Instant.now())
                    .build();

            OrderEntity entity = OrderEntity.builder()
                    .id(UUID.randomUUID())
                    .externalOrderId("EXT-001")
                    .totalAmount(BigDecimal.valueOf(200))
                    .totalCurrency("BRL")
                    .status(OrderStatusEntity.RECEIVED)
                    .items(List.of(itemEntity))
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .version(0L)
                    .build();

            // Act
            Order result = mapper.toDomain(entity);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(entity.getId());
            assertThat(result.getExternalOrderIdValue()).isEqualTo("EXT-001");
            assertThat(result.getTotalAmount().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(200));
            assertThat(result.getTotalAmount().getCurrency().getCurrencyCode()).isEqualTo("BRL");
            assertThat(result.getStatus()).isEqualTo(OrderStatus.RECEIVED);
            assertThat(result.getItems()).hasSize(1);
        }

        @Test
        @DisplayName("Deve retornar null quando OrderEntity é null")
        void shouldReturnNullWhenEntityIsNull() {
            // Act
            Order result = mapper.toDomain(null);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Testes de toItemEntity")
    class ToItemEntityTests {

        @Test
        @DisplayName("Deve converter OrderItem para OrderItemEntity")
        void shouldConvertOrderItemToEntity() {
            // Arrange
            OrderItem item = OrderItem.builder()
                    .id(UUID.randomUUID())
                    .productId(ProductId.of("PROD-001"))
                    .productName("Produto Teste")
                    .unitPrice(Money.of(BigDecimal.valueOf(100), Currency.getInstance("BRL")))
                    .quantity(2)
                    .createdAt(Instant.now())
                    .build();

            OrderEntity orderEntity = OrderEntity.builder()
                    .id(UUID.randomUUID())
                    .build();

            // Act
            OrderItemEntity result = mapper.toItemEntity(item, orderEntity);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(item.getId());
            assertThat(result.getProductId()).isEqualTo("PROD-001");
            assertThat(result.getProductName()).isEqualTo("Produto Teste");
            assertThat(result.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));
            assertThat(result.getUnitCurrency()).isEqualTo("BRL");
            assertThat(result.getQuantity()).isEqualTo(2);
            assertThat(result.getOrder()).isEqualTo(orderEntity);
        }

        @Test
        @DisplayName("Deve retornar null quando OrderItem é null")
        void shouldReturnNullWhenItemIsNull() {
            // Act
            OrderItemEntity result = mapper.toItemEntity(null, null);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Testes de toItemDomain")
    class ToItemDomainTests {

        @Test
        @DisplayName("Deve converter OrderItemEntity para OrderItem")
        void shouldConvertEntityToOrderItem() {
            // Arrange
            OrderItemEntity entity = OrderItemEntity.builder()
                    .id(UUID.randomUUID())
                    .productId("PROD-001")
                    .productName("Produto Teste")
                    .unitPrice(BigDecimal.valueOf(100))
                    .unitCurrency("BRL")
                    .quantity(2)
                    .subtotal(BigDecimal.valueOf(200))
                    .subtotalCurrency("BRL")
                    .createdAt(Instant.now())
                    .build();

            // Act
            OrderItem result = mapper.toItemDomain(entity);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(entity.getId());
            assertThat(result.getProductIdValue()).isEqualTo("PROD-001");
            assertThat(result.getProductName()).isEqualTo("Produto Teste");
            assertThat(result.getUnitPrice().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
            assertThat(result.getUnitPrice().getCurrency().getCurrencyCode()).isEqualTo("BRL");
            assertThat(result.getQuantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("Deve retornar null quando OrderItemEntity é null")
        void shouldReturnNullWhenItemEntityIsNull() {
            // Act
            OrderItem result = mapper.toItemDomain(null);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Testes de toDomainList")
    class ToDomainListTests {

        @Test
        @DisplayName("Deve converter lista de OrderEntity para lista de Order")
        void shouldConvertEntityListToOrderList() {
            // Arrange
            OrderItemEntity itemEntity = OrderItemEntity.builder()
                    .id(UUID.randomUUID())
                    .productId("PROD-001")
                    .productName("Produto")
                    .unitPrice(BigDecimal.valueOf(100))
                    .unitCurrency("BRL")
                    .quantity(1)
                    .subtotal(BigDecimal.valueOf(100))
                    .subtotalCurrency("BRL")
                    .createdAt(Instant.now())
                    .build();

            OrderEntity entity1 = OrderEntity.builder()
                    .id(UUID.randomUUID())
                    .externalOrderId("EXT-001")
                    .totalAmount(BigDecimal.valueOf(100))
                    .totalCurrency("BRL")
                    .status(OrderStatusEntity.RECEIVED)
                    .items(List.of(itemEntity))
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .version(0L)
                    .build();

            OrderEntity entity2 = OrderEntity.builder()
                    .id(UUID.randomUUID())
                    .externalOrderId("EXT-002")
                    .totalAmount(BigDecimal.valueOf(200))
                    .totalCurrency("BRL")
                    .status(OrderStatusEntity.CALCULATED)
                    .items(List.of(itemEntity))
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .version(0L)
                    .build();

            List<OrderEntity> entities = List.of(entity1, entity2);

            // Act
            List<Order> result = mapper.toDomainList(entities);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getExternalOrderIdValue()).isEqualTo("EXT-001");
            assertThat(result.get(1).getExternalOrderIdValue()).isEqualTo("EXT-002");
        }
    }

    @Nested
    @DisplayName("Testes de toStatusEntity")
    class ToStatusEntityTests {

        @Test
        @DisplayName("Deve converter OrderStatus.RECEIVED para OrderStatusEntity.RECEIVED")
        void shouldConvertReceivedStatus() {
            // Act
            OrderStatusEntity result = mapper.toStatusEntity(OrderStatus.RECEIVED);

            // Assert
            assertThat(result).isEqualTo(OrderStatusEntity.RECEIVED);
        }

        @Test
        @DisplayName("Deve converter OrderStatus.PROCESSING para OrderStatusEntity.PROCESSING")
        void shouldConvertProcessingStatus() {
            // Act
            OrderStatusEntity result = mapper.toStatusEntity(OrderStatus.PROCESSING);

            // Assert
            assertThat(result).isEqualTo(OrderStatusEntity.PROCESSING);
        }

        @Test
        @DisplayName("Deve converter OrderStatus.CALCULATED para OrderStatusEntity.CALCULATED")
        void shouldConvertCalculatedStatus() {
            // Act
            OrderStatusEntity result = mapper.toStatusEntity(OrderStatus.CALCULATED);

            // Assert
            assertThat(result).isEqualTo(OrderStatusEntity.CALCULATED);
        }

        @Test
        @DisplayName("Deve converter OrderStatus.AVAILABLE para OrderStatusEntity.AVAILABLE")
        void shouldConvertAvailableStatus() {
            // Act
            OrderStatusEntity result = mapper.toStatusEntity(OrderStatus.AVAILABLE);

            // Assert
            assertThat(result).isEqualTo(OrderStatusEntity.AVAILABLE);
        }

        @Test
        @DisplayName("Deve converter OrderStatus.FAILED para OrderStatusEntity.FAILED")
        void shouldConvertFailedStatus() {
            // Act
            OrderStatusEntity result = mapper.toStatusEntity(OrderStatus.FAILED);

            // Assert
            assertThat(result).isEqualTo(OrderStatusEntity.FAILED);
        }

        @Test
        @DisplayName("Deve retornar RECEIVED quando status é null")
        void shouldReturnReceivedWhenStatusIsNull() {
            // Act
            OrderStatusEntity result = mapper.toStatusEntity(null);

            // Assert
            assertThat(result).isEqualTo(OrderStatusEntity.RECEIVED);
        }
    }

    @Nested
    @DisplayName("Testes de toStatusDomain")
    class ToStatusDomainTests {

        @Test
        @DisplayName("Deve converter OrderStatusEntity.RECEIVED para OrderStatus.RECEIVED")
        void shouldConvertReceivedEntity() {
            // Act
            OrderStatus result = mapper.toStatusDomain(OrderStatusEntity.RECEIVED);

            // Assert
            assertThat(result).isEqualTo(OrderStatus.RECEIVED);
        }

        @Test
        @DisplayName("Deve converter OrderStatusEntity.PROCESSING para OrderStatus.PROCESSING")
        void shouldConvertProcessingEntity() {
            // Act
            OrderStatus result = mapper.toStatusDomain(OrderStatusEntity.PROCESSING);

            // Assert
            assertThat(result).isEqualTo(OrderStatus.PROCESSING);
        }

        @Test
        @DisplayName("Deve converter OrderStatusEntity.CALCULATED para OrderStatus.CALCULATED")
        void shouldConvertCalculatedEntity() {
            // Act
            OrderStatus result = mapper.toStatusDomain(OrderStatusEntity.CALCULATED);

            // Assert
            assertThat(result).isEqualTo(OrderStatus.CALCULATED);
        }

        @Test
        @DisplayName("Deve converter OrderStatusEntity.AVAILABLE para OrderStatus.AVAILABLE")
        void shouldConvertAvailableEntity() {
            // Act
            OrderStatus result = mapper.toStatusDomain(OrderStatusEntity.AVAILABLE);

            // Assert
            assertThat(result).isEqualTo(OrderStatus.AVAILABLE);
        }

        @Test
        @DisplayName("Deve converter OrderStatusEntity.FAILED para OrderStatus.FAILED")
        void shouldConvertFailedEntity() {
            // Act
            OrderStatus result = mapper.toStatusDomain(OrderStatusEntity.FAILED);

            // Assert
            assertThat(result).isEqualTo(OrderStatus.FAILED);
        }

        @Test
        @DisplayName("Deve retornar RECEIVED quando status entity é null")
        void shouldReturnReceivedWhenStatusEntityIsNull() {
            // Act
            OrderStatus result = mapper.toStatusDomain(null);

            // Assert
            assertThat(result).isEqualTo(OrderStatus.RECEIVED);
        }
    }
}
