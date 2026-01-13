package com.order.domain.entity;

import com.order.domain.exception.ValidationException;
import com.order.domain.valueobject.Money;
import com.order.domain.valueobject.ProductId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de domínio que representa um item do pedido.
 * 
 * Responsabilidades:
 * - Validar dados do item
 * - Calcular subtotal (unitPrice * quantity)
 */
public class OrderItem {
    
    private final UUID id;
    private final ProductId productId;
    private final String productName;
    private final Money unitPrice;
    private final int quantity;
    private final Money subtotal;
    private final Instant createdAt;
    
    private OrderItem(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.productId = builder.productId;
        this.productName = builder.productName;
        this.unitPrice = builder.unitPrice;
        this.quantity = builder.quantity;
        this.subtotal = calculateSubtotal();
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
    }
    
    /**
     * Calcula o subtotal do item: preço unitário * quantidade.
     */
    private Money calculateSubtotal() {
        return unitPrice.multiply(quantity);
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public ProductId getProductId() {
        return productId;
    }
    
    public String getProductIdValue() {
        return productId.getValue();
    }
    
    public String getProductName() {
        return productName;
    }
    
    public Money getUnitPrice() {
        return unitPrice;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public Money getSubtotal() {
        return subtotal;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "OrderItem{id=%s, productId=%s, productName='%s', quantity=%d, subtotal=%s}".formatted(
            id, productId, productName, quantity, subtotal);
    }
    
    // Builder
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UUID id;
        private ProductId productId;
        private String productName;
        private Money unitPrice;
        private int quantity;
        private Instant createdAt;
        
        private Builder() {}
        
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
        
        public Builder unitPrice(Money unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }
        
        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }
        
        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public OrderItem build() {
            validate();
            return new OrderItem(this);
        }
        
        private void validate() {
            if (productId == null) {
                throw new ValidationException("ProductId é obrigatório");
            }
            if (productName == null || productName.isBlank()) {
                throw new ValidationException("Nome do produto é obrigatório");
            }
            if (productName.length() > 255) {
                throw new ValidationException("Nome do produto não pode exceder 255 caracteres");
            }
            if (unitPrice == null) {
                throw new ValidationException("Preço unitário é obrigatório");
            }
            if (!unitPrice.isPositive()) {
                throw new ValidationException("Preço unitário deve ser maior que zero");
            }
            if (quantity <= 0) {
                throw new ValidationException("Quantidade deve ser maior que zero");
            }
        }
    }
}
