package io.github.douglasdreer.order.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade JPA que representa a tabela 'orders'.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "external_order_id", nullable = false, unique = true, length = 100)
    private String externalOrderId;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "total_currency", nullable = false, length = 3)
    @Builder.Default
    private String totalCurrency = "BRL";

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatusEntity status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItemEntity> items = new ArrayList<>();

    /**
     * Adiciona um item ao pedido, mantendo a relação bidirecional.
     */
    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Remove um item do pedido.
     */
    public void removeItem(OrderItemEntity item) {
        items.remove(item);
        item.setOrder(null);
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
        if (status == null) {
            status = OrderStatusEntity.RECEIVED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
