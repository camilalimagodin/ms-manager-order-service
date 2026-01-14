package io.github.douglasdreer.order.domain.factory;

import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.entity.OrderItem;
import io.github.douglasdreer.order.domain.valueobject.OrderStatus;
import io.github.douglasdreer.order.domain.valueobject.ExternalOrderId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Factory para criar instâncias de Order para testes
 * Padrão inspirado em Kotlin com valores padrão
 */
public class OrderTestFactory {

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.RECEIVED;

    private OrderTestFactory() {
        // Utility class
    }

    /**
     * Cria Order com valores padrão:
     * - id: UUID gerado
     * - externalOrderId: EXT-001
     * - status: RECEIVED
     * - items: lista com um item padrão
     * - createdAt: Instant.now()
     */
    public static Order create() {
        return Order.builder()
                .externalOrderId(ExternalOrderIdTestFactory.create())
                .status(DEFAULT_STATUS)
                .addItem(OrderItemTestFactory.create())
                .build();
    }

    /**
     * Cria Order com externalOrderId customizado
     */
    public static Order create(ExternalOrderId externalOrderId) {
        return Order.builder()
                .externalOrderId(externalOrderId)
                .status(DEFAULT_STATUS)
                .addItem(OrderItemTestFactory.create())
                .build();
    }

    /**
     * Cria Order com externalOrderId e status customizados
     */
    public static Order create(ExternalOrderId externalOrderId, OrderStatus status) {
        return Order.builder()
                .externalOrderId(externalOrderId)
                .status(status)
                .addItem(OrderItemTestFactory.create())
                .build();
    }

    /**
     * Cria Order com externalOrderId, status e items customizados
     */
    public static Order create(ExternalOrderId externalOrderId, OrderStatus status, List<OrderItem> items) {
        Order.Builder orderBuilder = Order.builder()
                .externalOrderId(externalOrderId)
                .status(status);
        
        items.forEach(orderBuilder::addItem);
        
        return orderBuilder.build();
    }

    /**
     * Cria Order com todos os parâmetros customizados
     */
    public static Order create(UUID id, ExternalOrderId externalOrderId, OrderStatus status, List<OrderItem> items, Instant createdAt) {
        Order.Builder orderBuilder = Order.builder()
                .id(id)
                .externalOrderId(externalOrderId)
                .status(status)
                .createdAt(createdAt);
        
        items.forEach(orderBuilder::addItem);
        
        return orderBuilder.build();
    }

    /**
     * Cria Order com um item padrão
     */
    public static Order withSingleItem() {
        return create();
    }

    /**
     * Cria Order com múltiplos items padrão
     */
    public static Order withItems(int itemCount) {
        Order.Builder orderBuilder = Order.builder()
                .externalOrderId(ExternalOrderIdTestFactory.create())
                .status(DEFAULT_STATUS);
        
        for (int i = 0; i < itemCount; i++) {
            orderBuilder.addItem(OrderItemTestFactory.builder()
                    .productId(ProductIdTestFactory.unique())
                    .build());
        }
        
        return orderBuilder.build();
    }

    /**
     * Cria Order com status CALCULATED (processado)
     */
    public static Order calculated() {
        Order order = withSingleItem();
        order.startProcessing();
        order.calculateTotal();
        return order;
    }

    /**
     * Cria Order com status FAILED
     */
    public static Order failed() {
        Order order = create();
        order.markAsFailed();
        return order;
    }

    /**
     * Cria Order com status AVAILABLE
     */
    public static Order available() {
        Order order = calculated();
        order.markAsAvailable();
        return order;
    }

    /**
     * Builder para criação fluente de Order
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id = UUID.randomUUID();
        private ExternalOrderId externalOrderId = ExternalOrderIdTestFactory.create();
        private OrderStatus status = DEFAULT_STATUS;
        private List<OrderItem> items = new ArrayList<>();
        private Instant createdAt = Instant.now();

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder externalOrderId(ExternalOrderId externalOrderId) {
            this.externalOrderId = externalOrderId;
            return this;
        }

        public Builder externalOrderId(String externalOrderId) {
            this.externalOrderId = ExternalOrderId.of(externalOrderId);
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder items(List<OrderItem> items) {
            this.items = new ArrayList<>(items);
            return this;
        }

        public Builder addItem(OrderItem item) {
            this.items.add(item);
            return this;
        }

        public Builder withSingleItem() {
            this.items.add(OrderItemTestFactory.create());
            return this;
        }

        public Builder withItems(int count) {
            for (int i = 0; i < count; i++) {
                this.items.add(OrderItemTestFactory.builder()
                        .productId(ProductIdTestFactory.unique())
                        .build());
            }
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Order build() {
            Order.Builder orderBuilder = Order.builder()
                    .id(id)
                    .externalOrderId(externalOrderId)
                    .status(status);
            
            if (createdAt != null) {
                orderBuilder.createdAt(createdAt);
            }
            
            items.forEach(orderBuilder::addItem);
            
            return orderBuilder.build();
        }
    }
}
