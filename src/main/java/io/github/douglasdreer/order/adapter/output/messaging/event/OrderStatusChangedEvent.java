package io.github.douglasdreer.order.adapter.output.messaging.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento representando uma mudança de status de pedido.
 * Este evento é publicado no RabbitMQ quando um status de pedido muda
 * e pode ser consumido por sistemas externos (Produto Externo B).
 */
@Builder
public record OrderStatusChangedEvent(
        @JsonProperty("order_id")
        @NotBlank(message = "Order ID é obrigatório")
        String orderId,

        @JsonProperty("previous_status")
        String previousStatus,

        @JsonProperty("current_status")
        @NotBlank(message = "Status atual é obrigatório")
        String currentStatus,

        @JsonProperty("customer_id")
        @NotBlank(message = "Customer ID é obrigatório")
        String customerId,

        @JsonProperty("changed_at")
        @NotNull(message = "Timestamp de mudança é obrigatório")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime changedAt,

        @JsonProperty("correlation_id")
        String correlationId
) implements Serializable {
}
