package io.github.douglasdreer.order.adapter.output.messaging.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Evento de criação de pedido consumido do RabbitMQ (Produto Externo A). */
@Builder
public record OrderCreatedEvent(
        @JsonProperty("correlation_id")
        @NotBlank(message = "Correlation ID é obrigatório para rastreabilidade")
        String correlationId,

        @JsonProperty("customer_id")
        @NotBlank(message = "Customer ID é obrigatório")
        String customerId,

        @JsonProperty("items")
        @NotEmpty(message = "Pedido deve ter pelo menos um item")
        List<OrderItemEvent> items,

        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) implements Serializable {

    /** Item do pedido no evento. */
    @Builder
    public record OrderItemEvent(
            @JsonProperty("product_id")
            @NotBlank(message = "Product ID é obrigatório")
            String productId,

            @JsonProperty("quantity")
            @NotNull(message = "Quantidade é obrigatória")
            @Positive(message = "Quantidade deve ser positiva")
            Integer quantity,

            @JsonProperty("price")
            @NotNull(message = "Preço é obrigatório")
            @Positive(message = "Preço deve ser positivo")
            BigDecimal price
    ) implements Serializable {
    }
}
