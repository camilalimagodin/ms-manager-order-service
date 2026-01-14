package io.github.douglasdreer.order.application.mapper;

import io.github.douglasdreer.order.application.dto.CreateOrderCommand;
import io.github.douglasdreer.order.application.dto.OrderResponse;
import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.entity.OrderItem;
import io.github.douglasdreer.order.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;

/**
 * Mapper para conversão entre DTOs e entidades de domínio.
 */
@Component
public class OrderApplicationMapper {

    private static final String DEFAULT_CURRENCY = "BRL";

    /**
     * Converte CreateOrderCommand para Order (domínio).
     */
    public Order toDomain(CreateOrderCommand command) {
        if (command == null) {
            return null;
        }

        List<OrderItem> items = command.getItems().stream()
                .map(this::toItemDomain)
                .toList();

        return Order.builder()
                .externalOrderId(command.getExternalOrderId())
                .items(items)
                .build();
    }

    /**
     * Converte OrderItemCommand para OrderItem (domínio).
     */
    public OrderItem toItemDomain(CreateOrderCommand.OrderItemCommand command) {
        if (command == null) {
            return null;
        }

        String currencyCode = command.getCurrency() != null ? command.getCurrency() : DEFAULT_CURRENCY;
        Currency currency = Currency.getInstance(currencyCode);

        return OrderItem.builder()
                .productId(command.getProductId())
                .productName(command.getProductName())
                .unitPrice(Money.of(command.getUnitPrice(), currency))
                .quantity(command.getQuantity())
                .build();
    }

    /**
     * Converte Order (domínio) para OrderResponse.
     */
    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .externalOrderId(order.getExternalOrderIdValue())
                .totalAmount(order.getTotalAmount().getAmount())
                .currency(order.getTotalAmount().getCurrency().getCurrencyCode())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemResponses)
                .build();
    }

    /**
     * Converte OrderItem (domínio) para OrderItemResponse.
     */
    public OrderResponse.OrderItemResponse toItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }

        return OrderResponse.OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductIdValue())
                .productName(item.getProductName())
                .unitPrice(item.getUnitPrice().getAmount())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal().getAmount())
                .currency(item.getUnitPrice().getCurrency().getCurrencyCode())
                .build();
    }

    /**
     * Converte lista de Orders para lista de OrderResponse.
     */
    public List<OrderResponse> toResponseList(List<Order> orders) {
        return orders.stream()
                .map(this::toResponse)
                .toList();
    }
}
