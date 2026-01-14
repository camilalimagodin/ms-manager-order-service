package io.github.douglasdreer.order.domain.factory;

import io.github.douglasdreer.order.domain.entity.OrderItem;
import io.github.douglasdreer.order.domain.valueobject.Money;
import io.github.douglasdreer.order.domain.valueobject.ProductId;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Factory para criar instâncias de OrderItem para testes
 * Padrão inspirado em Kotlin com valores padrão
 */
public class OrderItemTestFactory {

    private static final int DEFAULT_QUANTITY = 1;
    private static final String DEFAULT_UNIT_PRICE = "100.00";
    private static final String DEFAULT_PRODUCT_NAME = "Produto Teste";

    private OrderItemTestFactory() {
        // Utility class
    }

    /**
     * Cria OrderItem com valores padrão:
     * - id: UUID gerado
     * - productId: PROD-001
     * - productName: "Produto Teste"
     * - quantity: 1
     * - unitPrice: BRL 100.00
     */
    public static OrderItem create() {
        return OrderItem.builder()
                .productId(ProductIdTestFactory.create())
                .productName(DEFAULT_PRODUCT_NAME)
                .quantity(DEFAULT_QUANTITY)
                .unitPrice(MoneyTestFactory.create(DEFAULT_UNIT_PRICE))
                .build();
    }

    /**
     * Cria OrderItem com productId customizado
     */
    public static OrderItem create(ProductId productId) {
        return OrderItem.builder()
                .productId(productId)
                .productName(DEFAULT_PRODUCT_NAME)
                .quantity(DEFAULT_QUANTITY)
                .unitPrice(MoneyTestFactory.create(DEFAULT_UNIT_PRICE))
                .build();
    }

    /**
     * Cria OrderItem com productId e quantity customizados
     */
    public static OrderItem create(ProductId productId, int quantity) {
        return OrderItem.builder()
                .productId(productId)
                .productName(DEFAULT_PRODUCT_NAME)
                .quantity(quantity)
                .unitPrice(MoneyTestFactory.create(DEFAULT_UNIT_PRICE))
                .build();
    }

    /**
     * Cria OrderItem com productId, quantity e unitPrice customizados
     */
    public static OrderItem create(ProductId productId, int quantity, Money unitPrice) {
        return OrderItem.builder()
                .productId(productId)
                .productName(DEFAULT_PRODUCT_NAME)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .build();
    }

    /**
     * Cria OrderItem com todos os parâmetros customizados
     */
    public static OrderItem create(UUID id, ProductId productId, int quantity, Money unitPrice) {
        return OrderItem.builder()
                .id(id)
                .productId(productId)
                .productName(DEFAULT_PRODUCT_NAME)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .build();
    }

    /**
     * Builder para criação fluente de OrderItem
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id = UUID.randomUUID();
        private ProductId productId = ProductIdTestFactory.create();
        private String productName = DEFAULT_PRODUCT_NAME;
        private int quantity = DEFAULT_QUANTITY;
        private Money unitPrice = MoneyTestFactory.create(DEFAULT_UNIT_PRICE);

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder productId(ProductId productId) {
            this.productId = productId;
            return this;
        }

        public Builder productId(String productId) {
            this.productId = ProductId.of(productId);
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder unitPrice(Money unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder unitPrice(String amount) {
            this.unitPrice = MoneyTestFactory.create(amount);
            return this;
        }

        public Builder unitPrice(BigDecimal amount) {
            this.unitPrice = MoneyTestFactory.create(amount);
            return this;
        }

        public OrderItem build() {
            return OrderItem.builder()
                    .id(id)
                    .productId(productId)
                    .productName(productName)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .build();
        }
    }
}
