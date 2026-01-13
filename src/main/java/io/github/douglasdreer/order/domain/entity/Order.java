package io.github.douglasdreer.order.domain.entity;

import io.github.douglasdreer.order.domain.exception.InvalidOrderStatusTransitionException;
import io.github.douglasdreer.order.domain.exception.ValidationException;
import io.github.douglasdreer.order.domain.valueobject.ExternalOrderId;
import io.github.douglasdreer.order.domain.valueobject.Money;
import io.github.douglasdreer.order.domain.valueobject.OrderStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade agregadora de domínio que representa um Pedido.
 * 
 * Responsabilidades:
 * - Gerenciar itens do pedido
 * - Calcular total do pedido
 * - Controlar transições de status
 * - Garantir invariantes do domínio
 */
public class Order {
    
    private final UUID id;
    private final ExternalOrderId externalOrderId;
    private final List<OrderItem> items;
    private Money totalAmount;
    private OrderStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private Long version;
    
    private Order(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.externalOrderId = builder.externalOrderId;
        this.items = new ArrayList<>(builder.items);
        this.status = builder.status != null ? builder.status : OrderStatus.RECEIVED;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : this.createdAt;
        this.version = builder.version != null ? builder.version : 0L;
        this.totalAmount = builder.totalAmount != null ? builder.totalAmount : Money.ZERO;
    }
    
    /**
     * Calcula o total do pedido somando os subtotais de todos os itens.
     * Atualiza o status para CALCULATED se bem sucedido.
     * 
     * @return o pedido com total calculado
     */
    public Order calculateTotal() {
        if (items.isEmpty()) {
            throw new ValidationException("Pedido deve ter pelo menos um item para calcular total");
        }
        
        this.totalAmount = items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(Money.ZERO, Money::add);
        
        this.updatedAt = Instant.now();
        
        if (this.status == OrderStatus.RECEIVED || this.status == OrderStatus.PROCESSING) {
            this.status = OrderStatus.CALCULATED;
        }
        
        return this;
    }
    
    /**
     * Transiciona o status do pedido para PROCESSING.
     */
    public Order startProcessing() {
        transitionTo(OrderStatus.PROCESSING);
        return this;
    }
    
    /**
     * Transiciona o status do pedido para AVAILABLE.
     */
    public Order markAsAvailable() {
        transitionTo(OrderStatus.AVAILABLE);
        return this;
    }
    
    /**
     * Transiciona o status do pedido para FAILED.
     */
    public Order markAsFailed() {
        // FAILED pode ser alcançado de qualquer status, exceto AVAILABLE
        if (this.status == OrderStatus.AVAILABLE) {
            throw new InvalidOrderStatusTransitionException(this.status, OrderStatus.FAILED);
        }
        this.status = OrderStatus.FAILED;
        this.updatedAt = Instant.now();
        return this;
    }
    
    /**
     * Transiciona para um novo status se permitido.
     */
    private void transitionTo(OrderStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new InvalidOrderStatusTransitionException(status, newStatus);
        }
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Adiciona um item ao pedido.
     * 
     * @param item item a ser adicionado
     */
    public void addItem(OrderItem item) {
        if (status != OrderStatus.RECEIVED) {
            throw new ValidationException("Não é possível adicionar itens após o pedido sair do status RECEIVED");
        }
        Objects.requireNonNull(item, "Item não pode ser nulo");
        this.items.add(item);
        this.updatedAt = Instant.now();
    }
    
    /**
     * Verifica se o pedido está disponível para consulta.
     */
    public boolean isAvailable() {
        return status == OrderStatus.AVAILABLE;
    }
    
    /**
     * Verifica se o pedido falhou.
     */
    public boolean isFailed() {
        return status == OrderStatus.FAILED;
    }
    
    /**
     * Retorna o número de itens no pedido.
     */
    public int getItemCount() {
        return items.size();
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public ExternalOrderId getExternalOrderId() {
        return externalOrderId;
    }
    
    public String getExternalOrderIdValue() {
        return externalOrderId.getValue();
    }
    
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
    
    public Money getTotalAmount() {
        return totalAmount;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public Long getVersion() {
        return version;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Order{id=%s, externalOrderId=%s, itemCount=%d, totalAmount=%s, status=%s}".formatted(
            id, externalOrderId, items.size(), totalAmount, status);
    }
    
    // Builder
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UUID id;
        private ExternalOrderId externalOrderId;
        private List<OrderItem> items = new ArrayList<>();
        private Money totalAmount;
        private OrderStatus status;
        private Instant createdAt;
        private Instant updatedAt;
        private Long version;
        
        private Builder() {}
        
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
        
        public Builder items(List<OrderItem> items) {
            this.items = new ArrayList<>(items);
            return this;
        }
        
        public Builder addItem(OrderItem item) {
            this.items.add(item);
            return this;
        }
        
        public Builder totalAmount(Money totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }
        
        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public Builder version(Long version) {
            this.version = version;
            return this;
        }
        
        public Order build() {
            validate();
            return new Order(this);
        }
        
        private void validate() {
            if (externalOrderId == null) {
                throw new ValidationException("ExternalOrderId é obrigatório");
            }
            if (items.isEmpty()) {
                throw new ValidationException("Pedido deve ter pelo menos um item");
            }
        }
    }
}
