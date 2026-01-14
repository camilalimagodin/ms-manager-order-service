package io.github.douglasdreer.order.infrastructure.persistence.adapter;

import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.entity.OrderItem;
import io.github.douglasdreer.order.domain.valueobject.*;
import io.github.douglasdreer.order.infrastructure.persistence.entity.OrderEntity;
import io.github.douglasdreer.order.infrastructure.persistence.entity.OrderStatusEntity;
import io.github.douglasdreer.order.infrastructure.persistence.mapper.OrderPersistenceMapper;
import io.github.douglasdreer.order.infrastructure.persistence.repository.OrderJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderRepositoryAdapter - Testes do adapter de persistência")
class OrderRepositoryAdapterTest {

    @Mock
    private OrderJpaRepository jpaRepository;

    @Mock
    private OrderPersistenceMapper mapper;

    @InjectMocks
    private OrderRepositoryAdapter repositoryAdapter;

    private Order createDomainOrder() {
        OrderItem item = OrderItem.builder()
                .id(UUID.randomUUID())
                .productId(ProductId.of("PROD-001"))
                .productName("Produto Teste")
                .unitPrice(Money.of(BigDecimal.valueOf(100), Currency.getInstance("BRL")))
                .quantity(2)
                .createdAt(Instant.now())
                .build();

        return Order.builder()
                .id(UUID.randomUUID())
                .externalOrderId(ExternalOrderId.of("EXT-001"))
                .items(List.of(item))
                .totalAmount(Money.of(BigDecimal.valueOf(200), Currency.getInstance("BRL")))
                .status(OrderStatus.RECEIVED)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .version(0L)
                .build();
    }

    private OrderEntity createJpaEntity() {
        return OrderEntity.builder()
                .id(UUID.randomUUID())
                .externalOrderId("EXT-001")
                .totalAmount(BigDecimal.valueOf(200))
                .totalCurrency("BRL")
                .status(OrderStatusEntity.RECEIVED)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .version(0L)
                .build();
    }

    @Nested
    @DisplayName("Testes de save")
    class SaveTests {

        @Test
        @DisplayName("Deve salvar pedido com sucesso")
        void shouldSaveOrderSuccessfully() {
            // Arrange
            Order domainOrder = createDomainOrder();
            OrderEntity jpaEntity = createJpaEntity();
            
            when(mapper.toEntity(domainOrder)).thenReturn(jpaEntity);
            when(jpaRepository.save(jpaEntity)).thenReturn(jpaEntity);
            when(mapper.toDomain(jpaEntity)).thenReturn(domainOrder);

            // Act
            Order result = repositoryAdapter.save(domainOrder);

            // Assert
            assertThat(result).isNotNull();
            verify(mapper).toEntity(domainOrder);
            verify(jpaRepository).save(jpaEntity);
            verify(mapper).toDomain(jpaEntity);
        }
    }

    @Nested
    @DisplayName("Testes de findById")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar pedido quando encontrado por ID")
        void shouldReturnOrderWhenFoundById() {
            // Arrange
            UUID id = UUID.randomUUID();
            OrderEntity jpaEntity = createJpaEntity();
            Order domainOrder = createDomainOrder();
            
            when(jpaRepository.findById(id)).thenReturn(Optional.of(jpaEntity));
            when(mapper.toDomain(jpaEntity)).thenReturn(domainOrder);

            // Act
            Optional<Order> result = repositoryAdapter.findById(id);

            // Assert
            assertThat(result)
                    .isPresent()
                    .contains(domainOrder);
            verify(jpaRepository).findById(id);
            verify(mapper).toDomain(jpaEntity);
        }

        @Test
        @DisplayName("Deve retornar vazio quando pedido não encontrado por ID")
        void shouldReturnEmptyWhenNotFoundById() {
            // Arrange
            UUID id = UUID.randomUUID();
            when(jpaRepository.findById(id)).thenReturn(Optional.empty());

            // Act
            Optional<Order> result = repositoryAdapter.findById(id);

            // Assert
            assertThat(result).isEmpty();
            verify(jpaRepository).findById(id);
            verify(mapper, never()).toDomain(any());
        }
    }

    @Nested
    @DisplayName("Testes de findByExternalOrderId")
    class FindByExternalOrderIdTests {

        @Test
        @DisplayName("Deve retornar pedido quando encontrado por ID externo")
        void shouldReturnOrderWhenFoundByExternalId() {
            // Arrange
            String externalId = "EXT-001";
            OrderEntity jpaEntity = createJpaEntity();
            Order domainOrder = createDomainOrder();
            
            when(jpaRepository.findByExternalOrderId(externalId)).thenReturn(Optional.of(jpaEntity));
            when(mapper.toDomain(jpaEntity)).thenReturn(domainOrder);

            // Act
            Optional<Order> result = repositoryAdapter.findByExternalOrderId(externalId);

            // Assert
            assertThat(result)
                    .isPresent()
                    .contains(domainOrder);
            verify(jpaRepository).findByExternalOrderId(externalId);
            verify(mapper).toDomain(jpaEntity);
        }

        @Test
        @DisplayName("Deve retornar vazio quando não encontrado por ID externo")
        void shouldReturnEmptyWhenNotFoundByExternalId() {
            // Arrange
            String externalId = "UNKNOWN";
            when(jpaRepository.findByExternalOrderId(externalId)).thenReturn(Optional.empty());

            // Act
            Optional<Order> result = repositoryAdapter.findByExternalOrderId(externalId);

            // Assert
            assertThat(result).isEmpty();
            verify(jpaRepository).findByExternalOrderId(externalId);
            verify(mapper, never()).toDomain(any());
        }
    }

    @Nested
    @DisplayName("Testes de existsByExternalOrderId")
    class ExistsByExternalOrderIdTests {

        @Test
        @DisplayName("Deve retornar true quando pedido existe")
        void shouldReturnTrueWhenOrderExists() {
            // Arrange
            String externalId = "EXT-001";
            when(jpaRepository.existsByExternalOrderId(externalId)).thenReturn(true);

            // Act
            boolean result = repositoryAdapter.existsByExternalOrderId(externalId);

            // Assert
            assertThat(result).isTrue();
            verify(jpaRepository).existsByExternalOrderId(externalId);
        }

        @Test
        @DisplayName("Deve retornar false quando pedido não existe")
        void shouldReturnFalseWhenOrderDoesNotExist() {
            // Arrange
            String externalId = "UNKNOWN";
            when(jpaRepository.existsByExternalOrderId(externalId)).thenReturn(false);

            // Act
            boolean result = repositoryAdapter.existsByExternalOrderId(externalId);

            // Assert
            assertThat(result).isFalse();
            verify(jpaRepository).existsByExternalOrderId(externalId);
        }
    }

    @Nested
    @DisplayName("Testes de findByStatus")
    class FindByStatusTests {

        @Test
        @DisplayName("Deve retornar lista de pedidos por status")
        void shouldReturnOrdersByStatus() {
            // Arrange
            OrderStatus status = OrderStatus.CALCULATED;
            OrderStatusEntity statusEntity = OrderStatusEntity.CALCULATED;
            List<OrderEntity> jpaEntities = List.of(createJpaEntity());
            List<Order> domainOrders = List.of(createDomainOrder());
            
            when(mapper.toStatusEntity(status)).thenReturn(statusEntity);
            when(jpaRepository.findByStatus(statusEntity)).thenReturn(jpaEntities);
            when(mapper.toDomainList(jpaEntities)).thenReturn(domainOrders);

            // Act
            List<Order> result = repositoryAdapter.findByStatus(status);

            // Assert
            assertThat(result).hasSize(1);
            verify(mapper).toStatusEntity(status);
            verify(jpaRepository).findByStatus(statusEntity);
            verify(mapper).toDomainList(jpaEntities);
        }
    }

    @Nested
    @DisplayName("Testes de findByCreatedAtBetween")
    class FindByCreatedAtBetweenTests {

        @Test
        @DisplayName("Deve retornar pedidos criados entre datas")
        void shouldReturnOrdersCreatedBetweenDates() {
            // Arrange
            Instant startDate = Instant.now().minusSeconds(86400);
            Instant endDate = Instant.now();
            List<OrderEntity> jpaEntities = List.of(createJpaEntity());
            List<Order> domainOrders = List.of(createDomainOrder());
            
            when(jpaRepository.findByCreatedAtBetween(startDate, endDate)).thenReturn(jpaEntities);
            when(mapper.toDomainList(jpaEntities)).thenReturn(domainOrders);

            // Act
            List<Order> result = repositoryAdapter.findByCreatedAtBetween(startDate, endDate);

            // Assert
            assertThat(result).hasSize(1);
            verify(jpaRepository).findByCreatedAtBetween(startDate, endDate);
            verify(mapper).toDomainList(jpaEntities);
        }
    }

    @Nested
    @DisplayName("Testes de findAll")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar todos os pedidos")
        void shouldReturnAllOrders() {
            // Arrange
            List<OrderEntity> jpaEntities = List.of(createJpaEntity(), createJpaEntity());
            List<Order> domainOrders = List.of(createDomainOrder(), createDomainOrder());
            
            when(jpaRepository.findAll()).thenReturn(jpaEntities);
            when(mapper.toDomainList(jpaEntities)).thenReturn(domainOrders);

            // Act
            List<Order> result = repositoryAdapter.findAll();

            // Assert
            assertThat(result).hasSize(2);
            verify(jpaRepository).findAll();
            verify(mapper).toDomainList(jpaEntities);
        }
    }

    @Nested
    @DisplayName("Testes de deleteById")
    class DeleteByIdTests {

        @Test
        @DisplayName("Deve deletar pedido por ID")
        void shouldDeleteOrderById() {
            // Arrange
            UUID id = UUID.randomUUID();
            doNothing().when(jpaRepository).deleteById(id);

            // Act
            repositoryAdapter.deleteById(id);

            // Assert
            verify(jpaRepository).deleteById(id);
        }
    }
}
