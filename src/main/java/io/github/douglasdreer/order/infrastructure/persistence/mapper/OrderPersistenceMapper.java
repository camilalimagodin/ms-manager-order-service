package io.github.douglasdreer.order.infrastructure.persistence.mapper;

import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.entity.OrderItem;
import io.github.douglasdreer.order.domain.valueobject.ExternalOrderId;
import io.github.douglasdreer.order.domain.valueobject.Money;
import io.github.douglasdreer.order.domain.valueobject.OrderStatus;
import io.github.douglasdreer.order.domain.valueobject.ProductId;
import io.github.douglasdreer.order.infrastructure.persistence.entity.OrderEntity;
import io.github.douglasdreer.order.infrastructure.persistence.entity.OrderItemEntity;
import io.github.douglasdreer.order.infrastructure.persistence.entity.OrderStatusEntity;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;

/**
 * Mapper para conversão entre entidades de domínio e entidades JPA.
 */
@Component
public class OrderPersistenceMapper {

    /**
     * Converte Order (domínio) para OrderEntity (JPA).
     */
    public OrderEntity toEntity(Order domain) {
        if (domain == null) {
            return null;
        }

        OrderEntity entity = OrderEntity.builder()
                .id(domain.getId())
                .externalOrderId(domain.getExternalOrderIdValue())
                .totalAmount(domain.getTotalAmount().getAmount())
                .totalCurrency(domain.getTotalAmount().getCurrency().getCurrencyCode())
                .status(toStatusEntity(domain.getStatus()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .version(domain.getVersion())
                .build();

        // Mapear itens
        List<OrderItemEntity> itemEntities = domain.getItems().stream()
                .map(item -> toItemEntity(item, entity))
                .toList();
        
        entity.setItems(itemEntities);

        return entity;
    }

    /**
     * Converte OrderEntity (JPA) para Order (domínio).
     */
    public Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        // Mapear itens primeiro
        List<OrderItem> items = entity.getItems().stream()
                .map(this::toItemDomain)
                .toList();

        return Order.builder()
                .id(entity.getId())
                .externalOrderId(ExternalOrderId.of(entity.getExternalOrderId()))
                .items(items)
                .totalAmount(Money.of(entity.getTotalAmount(), Currency.getInstance(entity.getTotalCurrency())))
                .status(toStatusDomain(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    /**
     * Converte OrderItem (domínio) para OrderItemEntity (JPA).
     */
    public OrderItemEntity toItemEntity(OrderItem domain, OrderEntity orderEntity) {
        if (domain == null) {
            return null;
        }

        return OrderItemEntity.builder()
                .id(domain.getId())
                .order(orderEntity)
                .productId(domain.getProductIdValue())
                .productName(domain.getProductName())
                .unitPrice(domain.getUnitPrice().getAmount())
                .unitCurrency(domain.getUnitPrice().getCurrency().getCurrencyCode())
                .quantity(domain.getQuantity())
                .subtotal(domain.getSubtotal().getAmount())
                .subtotalCurrency(domain.getSubtotal().getCurrency().getCurrencyCode())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    /**
     * Converte OrderItemEntity (JPA) para OrderItem (domínio).
     */
    public OrderItem toItemDomain(OrderItemEntity entity) {
        if (entity == null) {
            return null;
        }

        return OrderItem.builder()
                .id(entity.getId())
                .productId(ProductId.of(entity.getProductId()))
                .productName(entity.getProductName())
                .unitPrice(Money.of(entity.getUnitPrice(), Currency.getInstance(entity.getUnitCurrency())))
                .quantity(entity.getQuantity())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Converte lista de OrderEntity para lista de Order.
     */
    public List<Order> toDomainList(List<OrderEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * Converte OrderStatus (domínio) para OrderStatusEntity (JPA).
     */
    public OrderStatusEntity toStatusEntity(OrderStatus status) {
        if (status == null) {
            return OrderStatusEntity.RECEIVED;
        }
        return switch (status) {
            case RECEIVED -> OrderStatusEntity.RECEIVED;
            case PROCESSING -> OrderStatusEntity.PROCESSING;
            case CALCULATED -> OrderStatusEntity.CALCULATED;
            case AVAILABLE -> OrderStatusEntity.AVAILABLE;
            case FAILED -> OrderStatusEntity.FAILED;
        };
    }

    /**
     * Converte OrderStatusEntity (JPA) para OrderStatus (domínio).
     */
    public OrderStatus toStatusDomain(OrderStatusEntity status) {
        if (status == null) {
            return OrderStatus.RECEIVED;
        }
        return switch (status) {
            case RECEIVED -> OrderStatus.RECEIVED;
            case PROCESSING -> OrderStatus.PROCESSING;
            case CALCULATED -> OrderStatus.CALCULATED;
            case AVAILABLE -> OrderStatus.AVAILABLE;
            case FAILED -> OrderStatus.FAILED;
        };
    }
}
