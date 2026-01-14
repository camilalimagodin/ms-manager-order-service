package io.github.douglasdreer.order.adapter.output.messaging.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Evento de mudança de status publicado no RabbitMQ para sistemas externos. */
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
